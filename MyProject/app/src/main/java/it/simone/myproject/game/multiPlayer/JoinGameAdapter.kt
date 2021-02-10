package it.simone.myproject.game.multiPlayer

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import it.simone.myproject.BackButton
import it.simone.myproject.MainActivity
import it.simone.myproject.R
import it.simone.myproject.gameserver.api.GameserverApi
import it.simone.myproject.gameserver.model.Game
import it.simone.myproject.login.LoginFragment
import kotlinx.android.synthetic.main.item_join_game.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JoinGameAdapter(
        private val context: Context,
        private val navController: NavController,
        private val loading_view: ConstraintLayout
        ) : RecyclerView.Adapter<JoinGameAdapter.ViewHolder> () {

    private var list: List<Game> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_join_game, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.view_player_name.text = list[position].player1.name

        holder.itemView.view_player_name.setOnClickListener {
            loading_view.visibility = View.VISIBLE
            GlobalScope.launch {
                val game = GameserverApi().joinGame(
                        LoginFragment.firebaseAuth.currentUser?.displayName.toString(),
                        list[position])

                withContext(Dispatchers.Main) {
                    loading_view.visibility = View.GONE
                }

                if (game != null) {
                    MultiPlayerGameView.game = game
                    MultiPlayerGameView.playerNum = 2
                    MainActivity.backButton = BackButton.DISABLED
                    navController.navigate(R.id.action_JoinGameFragment_to_MultiPlayerGameFragment)
                } else {
                    withContext(Dispatchers.Main) {
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Error!")
                        builder.setMessage("Failed to join game")
                        val alertDialog: AlertDialog = builder.create()
                        alertDialog.show()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun update(newList: List<Game>) {
        list = newList
        this.notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
}
