package it.simone.myproject.game.multiPlayer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import it.simone.myproject.BackButton
import it.simone.myproject.MainActivity
import it.simone.myproject.R
import it.simone.myproject.gameserver.api.GameserverApi
import it.simone.myproject.gameserver.model.GameState
import it.simone.myproject.login.LoginFragment
import kotlinx.android.synthetic.main.fragment_join_game.*
import kotlinx.coroutines.*

class HostGameFragment: Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_host_game, container, false)
    }

    private var waitLoop: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        waitLoop = GlobalScope.launch {
            val game = GameserverApi().createGame(LoginFragment.firebaseAuth.currentUser?.displayName.toString())

            if (game != null) {
                while (isActive) {
                    val newGame = GameserverApi().getGame(game.id)
                    if (newGame != null && newGame.state == GameState.PLAYING) break
                    delay(1000L)
                }

                MultiPlayerGameView.game = game
                MultiPlayerGameView.playerNum = 1
                MainActivity.backButton = BackButton.DISABLED
                findNavController().navigate(R.id.action_HostGameFragment_to_MultiPlayerGameFragment)

            } else {
                // ERROR
                findNavController().navigate(R.id.action_HostGameFragment_to_MultiPlayerMenuFragment)
            }

        }
    }

    override fun onPause() {
        super.onPause()

        waitLoop?.cancel()
    }
}