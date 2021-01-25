package it.simone.myproject.globalstats.authentication

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthenticationApi {
    private val service: AuthenticationApiService

    private val baseUrl = "https://api.globalstats.io/"
    private val clientId = "ZYwfNkk3MfwNrvvUDRV0wWq9KAGqnYknXQCLbXOX"
    private val clientSecret = "kLEQgzt8vuEsUNcvuCStWgqNSbYQZW3kIIaYiE7O"

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(AuthenticationApiService::class.java)
    }

    fun getToken(): String {
        val token = service.getToken(clientId, clientSecret, "endpoint_client", "client_credentials").execute()
        if (token.isSuccessful) return token.body()!!.access_token
        return AccessTokenProvider.default
    }
}