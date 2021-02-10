package it.simone.myproject.gameserver.api

import com.haroldadmin.cnradapter.NetworkResponse
import it.simone.myproject.gameserver.model.ErrorResponse
import it.simone.myproject.gameserver.model.Game
import retrofit2.http.*

interface GameserverApiService {
    @GET("/")
    suspend fun getGames(): NetworkResponse<List<Game>, ErrorResponse>

    @FormUrlEncoded
    @POST("/")
    suspend fun createGame(
            @Field("player_name") playerName: String
    ): NetworkResponse<Game, ErrorResponse>

    @GET("/{game_id}")
    suspend fun getGame(
            @Path("game_id") gameId: String
    ): NetworkResponse<Game, ErrorResponse>

    @FormUrlEncoded
    @POST("/{game_id}")
    suspend fun joinGame(
            @Field("player_name") playerName: String,
            @Path("game_id") gameId: String
    ): NetworkResponse<Game, ErrorResponse>

    @FormUrlEncoded
    @PUT("/{game_id}")
    suspend fun updateGame(
            @Field("player") player: Int,
            @Field("score") score: Int,
            @Field("state") state: Int,
            @Path("game_id") gameId: String
    ): NetworkResponse<Game, ErrorResponse>
}