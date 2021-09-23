package me.brisson.guardian.ui.fragments.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import me.brisson.guardian.data.repository.AuthRepository
import me.brisson.guardian.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    val emailError = MutableLiveData<Boolean>().apply { value = false }
    val passwordError = MutableLiveData<Boolean>().apply { value = false }

    private val signInWithEmailAndPasswordSuccessListener = MutableLiveData<Boolean>()

    fun getSignInWithEmailAndPasswordSuccessListener(): LiveData<Boolean> = signInWithEmailAndPasswordSuccessListener

    fun firebaseAuth() {
        authRepository.emailAndPasswordAuth(email.value!!, password.value!!)
            .addOnSuccessListener {
                signInWithEmailAndPasswordSuccessListener.value = true
                Log.d(TAG, "signInFirebase: Successfully")
            }
            .addOnFailureListener {
                signInWithEmailAndPasswordSuccessListener.value = false
                    Log.w(TAG, "signInFirebase: Failure", it)
            }
    }

    companion object {
        private val TAG = LoginViewModel::class.java.simpleName
    }


}