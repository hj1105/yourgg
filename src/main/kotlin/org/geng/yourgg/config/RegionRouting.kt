package org.geng.yourgg.config

object RegionRouting {
    private val platformToRegional = mapOf(
        "kr" to "asia",
        "jp1" to "asia",
        "na1" to "americas",
        "br1" to "americas",
        "la1" to "americas",
        "la2" to "americas",
        "euw1" to "europe",
        "eun1" to "europe",
        "tr1" to "europe",
        "ru" to "europe"
    )

    fun getRegionalHost(platformId: String): String {
        return platformToRegional[platformId.toLowerCase()]
            ?: throw IllegalArgumentException("지원하지 않는 지역입니다: $platformId")
    }
}