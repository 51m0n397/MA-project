package it.simone.myproject.leaderboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.simone.myproject.R
import it.simone.myproject.globalstats.model.Player
import kotlinx.android.synthetic.main.item_leaderboard.view.*

class LeaderboardAdapter(private val list: List<Player>) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_leaderboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.view_rank.text = list[position].rank.toString()
        holder.itemView.view_name.text = list[position].name
        holder.itemView.view_score.text = list[position].value.toString()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
}
