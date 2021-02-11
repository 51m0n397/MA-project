package it.simone.myproject.gameserver.authentication

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import it.simone.myproject.login.LoginFragment
import okhttp3.*


class TokenRefreshAuthenticator(
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val token = LoginFragment.googleAccount.idToken ?: return null

        synchronized(this) {
            val newToken = LoginFragment.googleAccount.idToken

            if (response.request().header("Authorization") != null) {

                if (newToken != token) {
                    return response.request()
                            .newBuilder()
                            .removeHeader("Authorization")
                            .addHeader("Authorization", "Bearer $newToken")
                            .build()
                }

                val task = LoginFragment.googleSignInClient.silentSignIn()
                if (!task.isSuccessful || task.result == null) return null

                LoginFragment.googleAccount = task.result!!

                val updatedToken = LoginFragment.googleAccount.idToken ?: return null

                return response.request()
                        .newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", "Bearer $updatedToken")
                        .build()
            }
        }
        return null
    }
}