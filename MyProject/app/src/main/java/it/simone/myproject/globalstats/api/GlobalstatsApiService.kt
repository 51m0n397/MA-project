package it.simone.myproject.globalstats.api

import com.haroldadmin.cnradapter.NetworkResponse
import it.simone.myproject.globalstats.model.*
import retrofit2.http.*

interface GlobalstatsApiService {
    @POST("/v1/statistics")
    suspend fun createScore(
        @Body request: CreateScoreRequest
    ): NetworkResponse<ScoreResponse, ErrorResponse>

    @PUT("/v1/statistics/{id}")
    suspend fun updateScore(
        @Path("id") id: String,
        @Body request: UpdateScoreRequest
    ): NetworkResponse<ScoreResponse, ErrorResponse>

    @GET("/v1/statistics/{id}")
    suspend fun getUserStats(
        @Path("id") id: String
    ): NetworkResponse<StatsResponse, ErrorResponse>

    @POST("/v1/gtdleaderboard/score")
    suspend fun getLeaderboard(
        @Body request: LeaderboardRequest
    ): NetworkResponse<LeaderboardResponse, ErrorResponse>
}