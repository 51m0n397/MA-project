package it.simone.myproject.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import it.simone.myproject.R
import it.simone.myproject.login.LoginFragment.Companion.globalstatsId
import it.simone.myproject.globalstats.api.GlobalstatsApi
import kotlinx.android.synthetic.main.fragment_leaderboard.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LeaderboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leaderboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = this.requireContext()

        GlobalScope.launch {
            val stats = GlobalstatsApi().getUserStatistic(globalstatsId)
            val leaderboard = GlobalstatsApi().getLeaderboard(100)

            withContext(Dispatchers.Main) {
                view_my_highest_score.text = getString(R.string.my_highest_score) + " " + stats?.value
                view_my_rank.text = getString(R.string.my_rank) + " " + stats?.rank
                val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                view_leaderboard_list.addItemDecoration(itemDecoration)

                val adapter = LeaderboardAdapter(leaderboard)

                view_leaderboard_list.layoutManager = LinearLayoutManager(context)
                view_leaderboard_list.adapter = adapter
            }
        }


    }
}
