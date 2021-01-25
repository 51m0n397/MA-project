package it.simone.myproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.simone.myproject.LoginFragment.Companion.firebaseAuth
import it.simone.myproject.LoginFragment.Companion.googleSignInClient
import kotlinx.android.synthetic.main.fragment_menu.*

/**
 * Main menu fragment
 */
class MenuFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_logout.setOnClickListener { ""
            googleSignInClient.signOut().addOnCompleteListener {
                firebaseAuth.signOut()
                findNavController().popBackStack()
                findNavController().navigate(R.id.LoginFragment)
            }
        }

        button_leaderboard.setOnClickListener {
            findNavController().navigate(R.id.action_MenuFragment_to_LeaderboardFragment)
        }

        button_newgame.setOnClickListener {
            findNavController().navigate(R.id.action_MenuFragment_to_GameFragment)
        }
    }
}