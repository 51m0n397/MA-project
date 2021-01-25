package it.simone.myproject.globalstats.model

data class StatsResponse(
    val name: String,
    val statistics: List<Statistic>
)
