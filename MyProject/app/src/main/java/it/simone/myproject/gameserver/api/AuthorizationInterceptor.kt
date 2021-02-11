package it.simone.myproject.gameserver.api

import it.simone.myproject.login.LoginFragment
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = LoginFragment.googleAccount.idToken

        return if (token == null) {
            chain.proceed(chain.request())
        } else {
            val authenticatedRequest = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            chain.proceed(authenticatedRequest)
        }
    }
}