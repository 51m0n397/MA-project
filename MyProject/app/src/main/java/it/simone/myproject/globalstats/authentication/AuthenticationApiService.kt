package it.simone.myproject.globalstats.authentication

import it.simone.myproject.globalstats.model.Token
import retrofit2.Call
import retrofit2.http.*

interface AuthenticationApiService {
    @POST("/oauth/access_token")
    @FormUrlEncoded
    fun getToken(
        @Field("client_id") client_id: String?,
        @Field("client_secret") client_secret: String?,
        @Field("scope") scope: String?,
        @Field("grant_type") grant_type: String?
    ): Call<Token>
}