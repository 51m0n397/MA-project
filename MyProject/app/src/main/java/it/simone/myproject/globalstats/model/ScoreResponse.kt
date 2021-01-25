package it.simone.myproject.globalstats.model

data class ScoreResponse(
    val name: String,
    val _id: String,
    val values: List<Value>,
    val achievements: List<Achievement>
)
