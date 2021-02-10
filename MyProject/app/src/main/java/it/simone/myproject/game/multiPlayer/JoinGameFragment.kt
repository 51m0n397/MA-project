package it.simone.myproject.game.multiPlayer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import it.simone.myproject.R
import it.simone.myproject.gameserver.api.GameserverApi
import kotlinx.android.synthetic.main.fragment_join_game.*
import kotlinx.coroutines.*

class JoinGameFragment: Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_join_game, container, false)
    }

    private var getGamesLoop: Job? = null
    private var adapter: JoinGameAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        view_games_list.addItemDecoration(itemDecoration)
        adapter = JoinGameAdapter(this.requireContext(), findNavController(), view_loading)
        view_games_list.layoutManager = LinearLayoutManager(context)
        view_games_list.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        getGamesLoop = GlobalScope.launch {
            while (isActive) {
                val games = GameserverApi().getPendingGames()
                Log.i("info", games.toString())

                withContext(Dispatchers.Main) {
                    if (games.size == 0) {
                        view_games_list.visibility = View.GONE
                        view_no_games.visibility = View.VISIBLE
                    } else {
                        view_games_list.visibility = View.VISIBLE
                        view_no_games.visibility = View.GONE
                    }
                    adapter?.update(games)
                }

                delay(1000L)
            }
        }
    }

    override fun onPause() {
        super.onPause()

        getGamesLoop?.cancel()
    }
}