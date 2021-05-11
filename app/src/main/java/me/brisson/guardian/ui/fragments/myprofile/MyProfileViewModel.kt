package me.brisson.guardian.ui.fragments.myprofile

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import me.brisson.guardian.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor() : BaseViewModel() {
    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()


}