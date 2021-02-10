package it.simone.myproject.game.multiPlayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class MultiPlayerGameFragment: Fragment() {
    lateinit var multiPlayerGameView : MultiPlayerGameView

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        multiPlayerGameView = MultiPlayerGameView(context)

        return multiPlayerGameView
    }

    override fun onResume() {
        super.onResume()

        multiPlayerGameView.play()
    }

    override fun onPause() {
        super.onPause()

        multiPlayerGameView.pause()
    }
}