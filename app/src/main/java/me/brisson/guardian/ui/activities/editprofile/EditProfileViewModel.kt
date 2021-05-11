package me.brisson.guardian.ui.activities.editprofile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import me.brisson.guardian.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(): BaseViewModel() {
    val photo = MutableLiveData<Uri>()
    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val newPassword = MutableLiveData<String>()

    val nameError = MutableLiveData<Boolean>().apply { value = false }
    val emailError = MutableLiveData<Boolean>().apply { value = false }
    val newPasswordError = MutableLiveData<Boolean>().apply { value = false }

    val changePassword = MutableLiveData<Boolean>().apply { value = false }

    fun onChangePasswordClick(){
        changePassword.value = !changePassword.value!!
    }
}