package me.brisson.guardian.ui.fragments.myprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import me.brisson.guardian.data.model.User
import me.brisson.guardian.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor() : BaseViewModel() {
    private val authUser: FirebaseUser? =  Firebase.auth.currentUser
    private val userRef = Firebase.firestore.collection("users")

    private val user = MutableLiveData<User>()
    fun getUser(): LiveData<User> = user

    fun setUser() {
        if (authUser != null){
            userRef
                .document(authUser.uid)
                .get()
                .addOnSuccessListener {
                    user.value = it.toObject(User::class.java)
                }
        }
    }


    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val photo = MutableLiveData<String>()

    fun logout(){
        if (authUser != null){
            userRef
                .document(authUser.uid)
                .update("online", false)
        }

        Firebase.auth.signOut()
    }

}