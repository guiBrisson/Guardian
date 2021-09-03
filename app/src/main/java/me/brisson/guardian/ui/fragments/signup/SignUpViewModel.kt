package me.brisson.guardian.ui.fragments.signup

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import me.brisson.guardian.data.model.User
import me.brisson.guardian.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor() : BaseViewModel() {
    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    val nameError = MutableLiveData<Boolean>().apply { value = false }
    val emailError = MutableLiveData<Boolean>().apply { value = false }
    val passwordError = MutableLiveData<Boolean>().apply { value = false }

    private val auth = Firebase.auth

    private val userCreatedSuccessListener = MutableLiveData<Boolean>()

    fun getAuth() = auth

    fun getUserCreatedSuccessListener() = userCreatedSuccessListener

    fun firebaseAuth() {
        auth.createUserWithEmailAndPassword(email.value!!, password.value!!)
            .addOnSuccessListener {
                // Sign in success
                Log.d(TAG, "createUserWithEmail: Successfully")
                val user = auth.currentUser
                updateUserName(user)
            }
            .addOnFailureListener {
                userCreatedSuccessListener.value = false
                Log.w(TAG, "createUserWithEmail:failure", it.cause)
            }

    }

    private fun updateUserName(user: FirebaseUser?) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name.value!!)
            .build()
        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task1 ->
                if (task1.isSuccessful) {

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
                    Log.w(TAG, "updateUserName: ", task1.exception)
                }
            }
    }

    private fun addNewUserToUsersCollection(newUser: User) {
        FirebaseFirestore.getInstance().collection("users")
            .document(newUser.uid) // document path
            .set(newUser)
            .addOnCompleteListener { task2 ->
                if (task2.isSuccessful) {
                    userCreatedSuccessListener.value = true
                    Log.d(
                        TAG, "User Added to users collection."
                    )
                }
                else {
                    userCreatedSuccessListener.value = false
                    Log.w(TAG, "Error adding user to user collection: ", task2.exception)
                }
            }

    }

    companion object {
        private val TAG = SignUpViewModel::class.java.simpleName
    }

}