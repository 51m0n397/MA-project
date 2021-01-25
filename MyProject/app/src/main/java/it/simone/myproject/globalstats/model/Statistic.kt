package it.simone.myproject.globalstats.model

data class Statistic(
    val key: String,
    val value: Number,
    val sorting: String,
    val rank: Int,
    val updated_at: String
)
