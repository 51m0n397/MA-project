package it.simone.myproject.globalstats.model

data class Token(
    val access_token: String,
    val token_type: String,
    val expires_in: Int
)
