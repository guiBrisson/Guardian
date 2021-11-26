package me.brisson.guardian.ui.fragments.signup

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import me.brisson.guardian.data.model.User
import me.brisson.guardian.data.repository.AuthRepository
import me.brisson.guardian.data.repository.UsersRepository
import me.brisson.guardian.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val usersRepository: UsersRepository
) : BaseViewModel() {
    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    val nameError = MutableLiveData<Boolean>().apply { value = false }
    val emailError = MutableLiveData<Boolean>().apply { value = false }
    val passwordError = MutableLiveData<Boolean>().apply { value = false }

    private val auth = Firebase.auth

    private val userCreatedSuccessListener = MutableLiveData<Boolean>()

    fun getUserCreatedSuccessListener() = userCreatedSuccessListener

    fun firebaseAuth() {
        authRepository.createUserWithEmailAndPassword(email.value!!, password.value!!)
            .addOnSuccessListener {
                // Sign in success
                Log.d(TAG, "createUserWithEmail: Successfully")
                val user = auth.currentUser
                updateFirebaseUserName(user!!)
            }
            .addOnFailureListener {
                userCreatedSuccessListener.value = false
                Log.w(TAG, "createUserWithEmail:failure", it.cause)
            }

    }

    private fun updateFirebaseUserName(user: FirebaseUser) {
        usersRepository.updateFirebaseUserName(user, name.value!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    // Mapping user for adding to the users collection.
                    val newUser = User(
                        uid = user.uid,
                        name = name.value!!,
                        email = email.value!!,
                        userImage = "",
                        online = true
                    )
                    addNewUserToUsersCollection(newUser)

                    Log.d(TAG, "User profile updated.")
                } else {
                    Log.w(TAG, "updateUserName: ", task.exception)
                }
            }
    }

    private fun addNewUserToUsersCollection(newUser: User) {
        usersRepository.addNewUserToUsersCollection(newUser)
            .addOnSuccessListener {
                userCreatedSuccessListener.value = true
                Log.d(
                    TAG, "User Added to users collection."
                )
            }
            .addOnFailureListener {
                userCreatedSuccessListener.value = false
                Log.w(TAG, "Error adding user to user collection: ", it)
            }
    }

    companion object {
        private val TAG = SignUpViewModel::class.java.simpleName
    }

}