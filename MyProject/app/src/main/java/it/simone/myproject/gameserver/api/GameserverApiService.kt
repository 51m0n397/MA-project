package it.simone.myproject.gameserver.api

import com.haroldadmin.cnradapter.NetworkResponse
import it.simone.myproject.gameserver.model.ErrorResponse
import it.simone.myproject.gameserver.model.Game
import retrofit2.http.*

interface GameserverApiService {
    @GET("/")
    suspend fun getGames(): NetworkResponse<List<Game>, ErrorResponse>

    @POST("/")
    suspend fun createGame(): NetworkResponse<Game, ErrorResponse>

    @GET("/{game_id}")
    suspend fun getGame(
            @Path("game_id") gameId: String
    ): NetworkResponse<Game, ErrorResponse>

    @POST("/{game_id}")
    suspend fun joinGame(
            @Path("game_id") gameId: String
    ): NetworkResponse<Game, ErrorResponse>

    @FormUrlEncoded
    @PUT("/{game_id}")
    suspend fun updateGame(
            @Field("score") score: Int,
            @Field("state") state: Int,
            @Field("bonus") bonus: Int?,
            @Path("game_id") gameId: String
    ): NetworkResponse<Game, ErrorResponse>
}