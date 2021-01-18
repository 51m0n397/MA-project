package it.simone.myproject

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.fragment_login.*


/**
 * Google login fragment
 */

class LoginFragment : Fragment() {

    private val RC_SIGN_IN = 9001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Conf.clientId)
            .build()

        Conf.googleSignInClient = GoogleSignIn.getClient(this.requireActivity(), gso);

        sign_in_button.setOnClickListener {
            signIn()
        }
    }

    override fun onStart() {
        super.onStart()

        val account = GoogleSignIn.getLastSignedInAccount(this.requireActivity())

        if (account != null) {
            Conf.token = account.idToken.toString()
            Conf.name = account.displayName.toString()
            Conf.familyName = account.familyName.toString()
            Conf.id = account.id.toString()

            findNavController(this).popBackStack()
            findNavController(this).navigate(R.id.MenuFragment)
        }
    }

    private fun signIn() {
        val signInIntent = Conf.googleSignInClient.signInIntent
        startActivityForResult(
            signInIntent, RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            Conf.token = account?.idToken.toString()
            Conf.name = account?.displayName.toString()
            Conf.familyName = account?.familyName.toString()
            Conf.id = account?.id.toString()

            findNavController(this).popBackStack()
            findNavController(this).navigate(R.id.MenuFragment)

        } catch (e: ApiException) {
            Log.e("Error", e.toString())

            val builder = AlertDialog.Builder(this.context)
            builder.setTitle("Error!")
            builder.setMessage("Login failed")
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
    }
}