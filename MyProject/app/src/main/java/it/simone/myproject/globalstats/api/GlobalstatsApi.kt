package it.simone.myproject.globalstats.api

import com.haroldadmin.cnradapter.NetworkResponse
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import it.simone.myproject.globalstats.authentication.*
import it.simone.myproject.globalstats.model.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//https://github.com/haroldadmin/NetworkResponseAdapter
//https://www.raywenderlich.com/6994782-android-networking-with-kotlin-tutorial-getting-started
//https://blog.coinbase.com/okhttp-oauth-token-refreshes-b598f55dd3b2
class GlobalstatsApi {

    private val service: GlobalstatsApiService

    private val baseUrl = "https://api.globalstats.io/"

    init {
        val accessTokenProvider = AccessTokenProvider()
        val authorizationInterceptor = AuthorizationInterceptor(accessTokenProvider)
        val tokenRefreshAuthenticator = TokenRefreshAuthenticator(accessTokenProvider)

        val client = OkHttpClient.Builder()
            .authenticator(tokenRefreshAuthenticator)
            .addInterceptor(authorizationInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        service = retrofit.create(GlobalstatsApiService::class.java)
    }

    suspend fun registerUser(name: String): String? {
        val response = service.createScore(CreateScoreRequest(name, Score(0)))
        if (response is NetworkResponse.Success) return response.body._id
        return null
    }

    suspend fun postScore(id: String, score: Int): Boolean {
        val response = service.updateScore(id, UpdateScoreRequest(Score(score)))
        if (response is NetworkResponse.Success) return true
        return false
    }

    suspend fun getUserStatistic(id: String): Statistic? {
        val response = service.getUserStats(id)
        if (response is NetworkResponse.Success) {
            val stats =  response.body.statistics
            for (s in stats) {
                if (s.key == "score") return s
            }
        }
        return null
    }

    suspend fun getLeaderboard(limit: Int): List<Player> {
        val response = service.getLeaderboard(LeaderboardRequest(limit))
        if (response is NetworkResponse.Success) return response.body.data
        return ArrayList<Player>()
    }
}