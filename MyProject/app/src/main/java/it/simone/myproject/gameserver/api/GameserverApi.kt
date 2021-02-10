package it.simone.myproject.gameserver.api

import android.util.Log
import com.haroldadmin.cnradapter.NetworkResponse
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import it.simone.myproject.gameserver.model.Game
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GameserverApi {
    private val service: GameserverApiService

    private val baseUrl = "http://192.168.1.124:5000/"

    init {
        val client = OkHttpClient.Builder()
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
        return ArrayList<Game>()
    }
}