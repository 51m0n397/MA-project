package it.simone.myproject

import android.content.Context
import android.content.SharedPreferences

class HighScoreManager(context: Context?) {
    private val defaultValue = 0
    private val filename = "high_score_file"
    private lateinit var sharedPref: SharedPreferences

    init {
        sharedPref = context?.getSharedPreferences(filename, Context.MODE_PRIVATE)!!
    }

    fun set(score: Int) {
        if (score > get()) {
            with(sharedPref.edit()) {
                putInt("high_score", score)
                apply()
            }
        }
    }

    fun get(): Int {
        return sharedPref.getInt("high_score", defaultValue)
    }

    fun clear() {
        set(defaultValue)
    }

}