package org.mcp_workshop.source.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinPriceResponse(
    val prices: Map<String, Map<String, Double>> = emptyMap()
)

@Serializable
data class TrendingResponse(
    val coins: List<TrendingCoinWrapper>
)

@Serializable
data class TrendingCoinWrapper(
    val item: TrendingCoin
)

@Serializable
data class TrendingCoin(
    val id: String,
    val name: String,
    val symbol: String,
    @SerialName("market_cap_rank") val marketCapRank: Int? = null,
    val data: TrendingCoinData? = null
)

@Serializable
data class TrendingCoinData(
    val price: Double? = null,
    @SerialName("price_change_percentage_24h") val priceChange24h: PriceChange24h? = null
)

@Serializable
data class PriceChange24h(
    val usd: Double? = null
)

@Serializable
data class CoinMarket(
    val id: String,
    val symbol: String,
    val name: String,
    @SerialName("current_price") val currentPrice: Double? = null,
    @SerialName("market_cap") val marketCap: Double? = null,
    @SerialName("market_cap_rank") val marketCapRank: Int? = null,
    @SerialName("price_change_percentage_24h") val priceChange24h: Double? = null,
    @SerialName("total_volume") val totalVolume: Double? = null
)
