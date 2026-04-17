package source.remote

private const val BASE_URL = "https://api.coingecko.com/api/v3"

// Returns price of a coin in the given currency
// coinId examples: "bitcoin", "ethereum", "solana"
// currency examples: "usd", "kes", "eur"

//suspend fun fetchCoinPrice(coinId: String, currency: String): Double? {
//    val client = httpClient()
//    val response = client.get("$BASE_URL/simple/price") {
//        parameter("ids", coinId)
//        parameter("vs_currencies", currency)
//    }
//    client.close()
//    val parsed = json.decodeFromString<JsonObject>(response.bodyAsText())
//    return parsed[coinId]?.jsonObject?.get(currency)?.toString()?.toDoubleOrNull()
//}
//
//suspend fun fetchTrendingCoins(): TrendingResponse {
//    val client = httpClient()
//    val response = client.get("$BASE_URL/search/trending")
//    client.close()
//    return json.decodeFromString(response.bodyAsText())
//}
//
//suspend fun fetchMarketOverview(currency: String = "usd", limit: Int = 10): List<CoinMarket> {
//    val client = httpClient()
//    val response = client.get("$BASE_URL/coins/markets") {
//        parameter("vs_currency", currency)
//        parameter("order", "market_cap_desc")
//        parameter("per_page", limit)
//        parameter("page", 1)
//    }
//    client.close()
//    return json.decodeFromString(response.bodyAsText())
//}