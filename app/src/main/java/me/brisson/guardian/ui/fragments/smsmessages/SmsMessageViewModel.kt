package me.brisson.guardian.ui.fragments.smsmessages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import me.brisson.guardian.data.model.Contact
import me.brisson.guardian.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class SmsMessageViewModel @Inject constructor() : BaseViewModel() {
    private val contacts = MutableLiveData<List<Contact>>()

    fun getContacts() : LiveData<List<Contact>> = contacts

    fun addContacts(items: ArrayList<Contact>){
        contacts.postValue(items)
    }


}