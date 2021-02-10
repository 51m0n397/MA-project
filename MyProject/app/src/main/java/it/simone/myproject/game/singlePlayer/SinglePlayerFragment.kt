package it.simone.myproject.game.singlePlayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class SinglePlayerFragment : Fragment() {

    lateinit var singlePlayerView : SinglePlayerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        singlePlayerView = SinglePlayerView(context)

        return singlePlayerView
    }

    override fun onResume() {
        super.onResume()

        singlePlayerView.play()
    }

    override fun onPause() {
        super.onPause()

        singlePlayerView.pause()
    }
}