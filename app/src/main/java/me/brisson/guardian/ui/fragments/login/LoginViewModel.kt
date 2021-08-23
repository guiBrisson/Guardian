package me.brisson.guardian.ui.fragments.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import me.brisson.guardian.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : BaseViewModel() {
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    val emailError = MutableLiveData<Boolean>().apply { value = false }
    val passwordError = MutableLiveData<Boolean>().apply { value = false }

    private val auth: FirebaseAuth = Firebase.auth

    private val signInWithEmailAndPasswordSuccessListener = MutableLiveData<Boolean>()

    fun getSignInWithEmailAndPasswordSuccessListener(): LiveData<Boolean> = signInWithEmailAndPasswordSuccessListener

    fun signInFirebase() {
        auth.signInWithEmailAndPassword(email.value!!, password.value!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    signInWithEmailAndPasswordSuccessListener.value = true
                    Log.d(TAG, "signInFirebase: Successfully")
                } else {
                    signInWithEmailAndPasswordSuccessListener.value = true
                    Log.w(TAG, "signInFirebase: Failure", task.exception)

                }
            }
    }

    companion object {
        private val TAG = LoginViewModel::class.java.simpleName
    }


}