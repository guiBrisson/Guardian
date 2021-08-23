package me.brisson.guardian.ui.activities.splash

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import me.brisson.guardian.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : BaseViewModel()  {

    private val user: FirebaseUser? = Firebase.auth.currentUser

    fun getUser(): FirebaseUser? = user
}