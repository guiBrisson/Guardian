package me.brisson.guardian.utils

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import me.brisson.guardian.R

open class SocialsLogin(val activity: Activity) {

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var callbackManager: CallbackManager
    private lateinit var buttonFacebookLogin: LoginButton


    //[START Google Sign In]
    fun initializeGoogleButton() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    fun handleGoogleResult(requestCode: Int, data: Intent?, isSuccessful: (Boolean) -> Unit) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!) {
                    isSuccessful(it)
                }
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, isSuccessful: (Boolean) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val auth = Firebase.auth
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    isSuccessful(true)
                    Log.d(TAG, "signInWithCredential:success")
                } else {
                    // If sign in fails, display a message to the user.
                    isSuccessful(false)
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }
    //[END Google Sign In]

    //[START Facebook Sign In]
    fun initializeFacebookButton(facebookLoginButton: LoginButton) {
        callbackManager = CallbackManager.Factory.create()

        buttonFacebookLogin = facebookLoginButton
        buttonFacebookLogin.setPermissions("email", "public_profile")

    }

    fun registerFacebookCallback(isSuccessful: (Boolean) -> Unit) {
        buttonFacebookLogin.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken) {
                    isSuccessful(it)
                }
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
                isSuccessful(false)
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken, isSuccessful: (Boolean) -> Unit) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val auth = Firebase.auth
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    isSuccessful(true)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        activity, task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    isSuccessful(false)
                }
            }
    }


    fun handleFacebookResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    //[END Facebook Sign In]

    companion object {
        private val TAG = SocialsLogin::class.java.simpleName
        private const val RC_SIGN_IN = 9001
    }

}