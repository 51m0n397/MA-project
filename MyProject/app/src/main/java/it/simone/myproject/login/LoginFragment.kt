package it.simone.myproject.login

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import it.simone.myproject.R
import it.simone.myproject.globalstats.api.GlobalstatsApi
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * Google login fragment
 */

class LoginFragment : Fragment() {
    companion object {
        val clientId = "410429140054-121fs1u1a15ijptjap8dfla1lh0gg4vs.apps.googleusercontent.com"
        lateinit var firebaseAuth: FirebaseAuth
        lateinit var googleSignInClient: GoogleSignInClient
        lateinit var googleAccount: GoogleSignInAccount
        lateinit var globalstatsId: String
    }

    private val RC_SIGN_IN = 9001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .build()

        googleSignInClient = GoogleSignIn.getClient(this.requireActivity(), gso)
        firebaseAuth = Firebase.auth

        sign_in_button.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
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

            if (account != null) firebaseAuthWithGoogle(account)

        } catch (e: ApiException) {
            Log.e("Error", e.toString())

            val builder = AlertDialog.Builder(this.context)
            builder.setTitle("Error!")
            builder.setMessage("Login failed")
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        googleAccount = account
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this.requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.i("info", "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    getGlobalstatsId(user!!)
                } else {
                    Log.i("info", "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun getGlobalstatsId(account: FirebaseUser) {
        val database = Firebase.database
        database.getReference("users").child(account.uid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userId = dataSnapshot.getValue(String::class.java)
                if (userId != null) {
                    Log.i("info", "user id: " + userId)
                    globalstatsId = userId
                    navigateToMenu()
                }
                else {
                    Log.i("info", "no user id, creating one")
                    GlobalScope.launch {
                        val new_id = GlobalstatsApi().registerUser(account.displayName.toString())
                        if (new_id == null) {
                            Log.i("info", "Error while retreiving user id")
                        } else {
                            val id = new_id.toString()
                            database.getReference("users").child(account.uid).setValue(id)
                            Log.i("info", "New id: " + id)
                            globalstatsId = id
                            navigateToMenu()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("info", "error: " + error)
            }
        })
    }

    private fun navigateToMenu() {
        findNavController(this).popBackStack()
        findNavController(this).navigate(R.id.MenuFragment)
    }
}