package it.simone.myproject.gameserver.api

import android.util.Log
import com.haroldadmin.cnradapter.NetworkResponse
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import it.simone.myproject.gameserver.authentication.AuthorizationInterceptor
import it.simone.myproject.gameserver.authentication.TokenRefreshAuthenticator
import it.simone.myproject.gameserver.model.Game
import it.simone.myproject.gameserver.model.PlayerState
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GameserverApi {
    private val service: GameserverApiService

    //private val baseUrl = "http://192.168.1.124:5000/"
    private val baseUrl = "http://51m0n397.pythonanywhere.com"

    init {
        val tokenRefreshAuthenticator = TokenRefreshAuthenticator()
        val authorizationInterceptor = AuthorizationInterceptor()

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
        service = retrofit.create(GameserverApiService::class.java)
    }

    suspend fun getPendingGames(): List<Game> {
        val response = service.getGames()
        if (response is NetworkResponse.Success) return response.body
        Log.i("info", "Error: " + response)
        return ArrayList()
    }

    suspend fun joinGame(game: Game): Game? {
        val response = service.joinGame(game.id)
        if (response is NetworkResponse.Success) return response.body
        Log.i("info", "Error: " + response)
        return null
    }

    suspend fun updateGame(score: Int, state: PlayerState, gameId: String): Game? {
        val response = service.updateGame(score, state.ordinal, gameId)
        if (response is NetworkResponse.Success) return response.body
        Log.i("info", "Error: " + response)
        return null
    }

    suspend fun getGame(gameId: String): Game? {
        val response = service.getGame(gameId)
        if (response is NetworkResponse.Success) return response.body
        Log.i("info", "Error: " + response)
        return null
    }

    suspend fun createGame(): Game? {
        val response = service.createGame()
        if (response is NetworkResponse.Success) return response.body
        Log.i("info", "Error: " + response)
        return null
    }

}