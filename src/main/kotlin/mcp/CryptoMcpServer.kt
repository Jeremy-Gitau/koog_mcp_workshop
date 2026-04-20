package mcp

import io.ktor.utils.io.streams.*
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import io.modelcontextprotocol.kotlin.sdk.types.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.io.asSink
import kotlinx.io.buffered
import kotlinx.serialization.json.*
import source.remote.fetchCoinPrice
import source.remote.fetchMarketOverview
import source.remote.fetchTrendingCoins

fun runCryptoMcpServer() {

    val server = Server(
        serverInfo = Implementation(name = "crypto-mcp-server", version = "1.0.0"),
        options = ServerOptions(
            capabilities = ServerCapabilities(
                tools = ServerCapabilities.Tools(listChanged = true)
            )
        )
    )

    val transport = StdioServerTransport(
        System.`in`.asInput(),
        System.out.asSink().buffered(),
    )

    // Tool 1: get_coin_price
    server.addTool(
        name = "get_coin_price",
        description = "Gets the current price of a cryptocurrency in a given currency.",
        inputSchema = ToolSchema(
            properties = buildJsonObject {
                putJsonObject("coinId") {
                    put("type", "string")
                    put("description", "CoinGecko coin id e.g. bitcoin, ethereum, solana, cardano")
                }
                putJsonObject("currency") {
                    put("type", "string")
                    put("description", "Target currency e.g. usd, kes, eur, gbp")
                }
            },
            required = listOf("coinId", "currency")
        )
    ) { request ->
        val coinId = request.arguments?.get("coinId")?.jsonPrimitive?.content
            ?: return@addTool CallToolResult(content = listOf(TextContent(text = "Parameter 'coinId' is required")))
        val currency = request.arguments?.get("currency")?.jsonPrimitive?.content
            ?: return@addTool CallToolResult(content = listOf(TextContent(text = "Parameter 'currency' is required")))

        val price = fetchCoinPrice(coinId, currency)
        val text = if (price != null)
            "Current price of $coinId: ${currency.uppercase()} $price"
        else
            "Could not fetch price for $coinId in $currency"

        CallToolResult(content = listOf(TextContent(text = text)))
    }

    // Tool 2: get_trending_coins
    server.addTool(
        name = "get_trending_coins",
        description = "Gets the top trending cryptocurrencies on CoinGecko right now.",
        inputSchema = ToolSchema(
            properties = buildJsonObject {},
            required = emptyList()
        )
    ) { _ ->
        val data = fetchTrendingCoins()
        val formatted = data.coins.take(7).joinToString("\n") { wrapper ->
            val coin = wrapper.item
            val price = coin.data?.price?.let { " — $%.4f USD".format(it) } ?: ""
            val change = coin.data?.priceChange24h?.usd?.let { " (24h: %.2f%%)".format(it) } ?: ""
            "#${coin.marketCapRank ?: "?"} ${coin.name} (${coin.symbol.uppercase()})$price$change"
        }
        CallToolResult(content = listOf(TextContent(text = "Trending Coins Right Now:\n$formatted")))
    }

    // Tool 3: get_market_overview
    server.addTool(
        name = "get_market_overview",
        description = "Gets the top cryptocurrencies by market cap with price and 24h change.",
        inputSchema = ToolSchema(
            properties = buildJsonObject {
                putJsonObject("currency") {
                    put("type", "string")
                    put("description", "Currency for prices e.g. usd, kes, eur")
                }
                putJsonObject("limit") {
                    put("type", "integer")
                    put("description", "Number of coins to return (default 10, max 20)")
                }
            },
            required = listOf("currency")
        )
    ) { request ->
        val currency = request.arguments?.get("currency")?.jsonPrimitive?.content
            ?: return@addTool CallToolResult(content = listOf(TextContent(text = "Parameter 'currency' is required")))
        val limit = (request.arguments?.get("limit")?.jsonPrimitive?.int ?: 10).coerceAtMost(20)

        val coins = fetchMarketOverview(currency, limit)
        val formatted = coins.joinToString("\n") { coin ->
            val price = coin.currentPrice?.let { "$it ${currency.uppercase()}" } ?: "N/A"
            val change = coin.priceChange24h?.let { "(24h: %.2f%%)".format(it) } ?: ""
            "#${coin.marketCapRank} ${coin.name} (${coin.symbol.uppercase()}) — $price $change"
        }
        CallToolResult(content = listOf(TextContent(text = "Top $limit Coins by Market Cap:\n$formatted")))
    }

    runBlocking {
        val session = server.createSession(transport)
        val done = Job()
        session.onClose { done.complete() }
        done.join()
    }
}