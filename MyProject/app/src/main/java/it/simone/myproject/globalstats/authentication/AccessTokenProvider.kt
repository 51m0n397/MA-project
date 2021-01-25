package it.simone.myproject.globalstats.authentication

import it.simone.myproject.MainActivity.Companion.prefs

class AccessTokenProvider {
    companion object {
        val default = "invalid"
    }
    private val key = "globalstats_token"
    private var token = default

    fun token(): String {
        token = prefs!!.getString(key, default).toString()
        return token
    }

    fun refreshToken(): String {
        token = AuthenticationApi().getToken()
        with(prefs!!.edit()) {
            putString(key, token)
            apply()
        }
        return token
    }
}