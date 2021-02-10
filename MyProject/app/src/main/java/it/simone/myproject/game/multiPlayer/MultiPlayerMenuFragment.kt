package it.simone.myproject.game.multiPlayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import it.simone.myproject.R
import kotlinx.android.synthetic.main.fragment_multi_player_menu.*

class MultiPlayerMenuFragment: Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_multi_player_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_host_game.setOnClickListener {
            findNavController().navigate(R.id.action_MultiPlayerMenuFragment_to_HostGameFragment)
        }

        button_join_game.setOnClickListener {
            findNavController().navigate(R.id.action_MultiPlayerMenuFragment_to_JoinGameFragment)
        }

    }
}