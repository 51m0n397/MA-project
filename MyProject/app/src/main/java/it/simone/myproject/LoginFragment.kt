package it.simone.myproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * Google login fragment
 */
class LoginFragment : Fragment() {

    val LOGIN_REQUEST = 1

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val clientId = Conf.clientId
        val request = GetSignInIntentRequest.builder()
                .setServerClientId(clientId)
                .build()

        val client = Identity.getSignInClient(this.requireActivity())

        sign_in_button.setOnClickListener {
            val intent = client.getSignInIntent(request)
            intent.addOnSuccessListener { request ->
                startIntentSenderForResult(request.intentSender,
                        LOGIN_REQUEST,
                        null,
                        0,
                        0,
                        0,
                        null)
                Log.i("info", "onCreate: ")
            }
            intent.addOnFailureListener { _ -> Log.i("info", "onFailure: ")}
        }
    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOGIN_REQUEST && resultCode == Activity.RESULT_OK) {
            val credential = Identity.getSignInClient(this.requireActivity())
                    .getSignInCredentialFromIntent(data)

            Conf.token = credential.googleIdToken.toString()
            Conf.name = credential.displayName.toString()
            Conf.familyName = credential.familyName.toString()
            Conf.id = credential.id.toString()

            findNavController(this).popBackStack()
            findNavController(this).navigate(R.id.MenuFragment)
        }
    }
}