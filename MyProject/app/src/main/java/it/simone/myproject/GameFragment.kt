package it.simone.myproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class GameFragment : Fragment() {

    lateinit var gameView : GameView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        gameView = GameView(context)

        return gameView
    }

    override fun onResume() {
        super.onResume()

        gameView.play()
    }

    override fun onPause() {
        super.onPause()

        gameView.pause()
    }
}