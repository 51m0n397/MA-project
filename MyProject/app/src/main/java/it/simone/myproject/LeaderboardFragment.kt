package it.simone.myproject

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import it.simone.myproject.LoginFragment.Companion.globalstatsId
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

        GlobalScope.launch {
            val score = GlobalstatsApi().getUserStatistic(globalstatsId)?.value

            withContext(Dispatchers.Main) {
                view_my_highest_score.text = getString(R.string.my_highest_score) + " " + score
            }
        }
    }
}
