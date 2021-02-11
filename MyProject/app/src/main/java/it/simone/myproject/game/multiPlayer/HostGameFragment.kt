package it.simone.myproject.game.multiPlayer

import android.app.AlertDialog
import android.os.Bundle
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
            val game = GameserverApi().createGame()

            if (game != null) {
                while (isActive) {
                    val newGame = GameserverApi().getGame(game.id)
                    if (newGame == null) {
                        withContext(Dispatchers.Main) {
                            val builder = AlertDialog.Builder(context)
                            builder.setTitle("Timeout!")
                            builder.setMessage("No one joined the game")
                            val alertDialog: AlertDialog = builder.create()
                            alertDialog.show()
                        }
                        findNavController().popBackStack()
                        break
                    } else if (newGame.state == GameState.PLAYING) {
                        MultiPlayerGameView.game = newGame
                        MultiPlayerGameView.playerNum = 1
                        MainActivity.backButton = BackButton.DISABLED
                        findNavController().navigate(R.id.action_HostGameFragment_to_MultiPlayerGameFragment)
                        break
                    }
                    delay(1000L)
                }

            } else {
                // ERROR
                withContext(Dispatchers.Main) {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Error!")
                    builder.setMessage("Failed to host game")
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.show()
                    findNavController().popBackStack()
                }
            }

        }
    }

    override fun onPause() {
        super.onPause()

        waitLoop?.cancel()
    }
}