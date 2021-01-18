package it.simone.myproject

import com.google.android.gms.auth.api.signin.GoogleSignInClient

class Conf {
    companion object {
        val clientId = "410429140054-121fs1u1a15ijptjap8dfla1lh0gg4vs.apps.googleusercontent.com"
        lateinit var googleSignInClient: GoogleSignInClient
        var token = ""
        var name = ""
        var familyName = ""
        var id = ""
    }
}