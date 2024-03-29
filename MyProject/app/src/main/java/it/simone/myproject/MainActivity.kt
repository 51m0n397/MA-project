package it.simone.myproject

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController

class MainActivity : AppCompatActivity() {
    companion object {
        var prefs: SharedPreferences? = null
        var context: Context? = null
        var backButton = BackButton.NORMAL
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = this.getPreferences(Context.MODE_PRIVATE)
        context = this.applicationContext
    }

    override fun onBackPressed() {
       when(backButton) {
           BackButton.NORMAL -> super.onBackPressed()
           BackButton.BACK_TO_MENU -> {
               findNavController(R.id.nav_host_fragment).popBackStack()
               findNavController(R.id.nav_host_fragment).navigate(R.id.MenuFragment)
               backButton = BackButton.NORMAL
           }
       }
    }
}