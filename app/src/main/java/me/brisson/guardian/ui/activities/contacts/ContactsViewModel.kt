package me.brisson.guardian.ui.activities.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import me.brisson.guardian.data.model.Contact
import me.brisson.guardian.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor() : BaseViewModel() {
    private val contacts = MutableLiveData<List<Contact>>()

    fun getContacts(): LiveData<List<Contact>> = contacts

    fun setContacts(contacts: List<Contact>) {
        this.contacts.postValue(contacts)
    }

    fun clearContacts() {
        contacts.value = emptyList()
    }

}