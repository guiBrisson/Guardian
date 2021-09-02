package me.brisson.guardian.ui.fragments.myprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import me.brisson.guardian.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor() : BaseViewModel() {
    val user =  MutableLiveData<FirebaseUser?>().apply { value = Firebase.auth.currentUser }

    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val photo = MutableLiveData<String>()

}