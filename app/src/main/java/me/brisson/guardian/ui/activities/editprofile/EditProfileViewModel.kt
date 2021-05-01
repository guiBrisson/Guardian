package me.brisson.guardian.ui.activities.editprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import me.brisson.guardian.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(): BaseViewModel() {

    private val changePassword = MutableLiveData<Boolean>().apply { value = false }

    fun getChangePassword() : LiveData<Boolean> = changePassword

    fun onChangePasswordClick(){
        changePassword.value = !changePassword.value!!
    }
}