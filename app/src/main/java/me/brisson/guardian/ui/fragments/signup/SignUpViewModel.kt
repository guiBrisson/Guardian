package me.brisson.guardian.ui.fragments.signup

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
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



}