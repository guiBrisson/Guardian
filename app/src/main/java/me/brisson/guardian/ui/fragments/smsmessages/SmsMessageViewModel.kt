package me.brisson.guardian.ui.fragments.smsmessages

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import me.brisson.guardian.data.model.Contact
import me.brisson.guardian.ui.base.BaseViewModel
import me.brisson.guardian.ui.fragments.appmessages.AppMessagesViewModel
import javax.inject.Inject

@HiltViewModel
class SmsMessageViewModel @Inject constructor() : BaseViewModel() {
    private val smsContacts = MutableLiveData<ArrayList<Contact>>()
    private val user = FirebaseAuth.getInstance().currentUser
    private val db = Firebase.firestore
    private val contactRef = db.collection("users").document(user!!.uid).collection("contacts")

    fun getContacts(): LiveData<ArrayList<Contact>> = smsContacts

    init {
        fetchContacts()
    }

    private fun fetchContacts() {
        contactRef
            .whereEqualTo("isPhoneContact", true) // Filtering app contacts
            .get()
            .addOnSuccessListener { contacts ->
                // Mapping and adding app contacts
                val contactSms = ArrayList<Contact>()
                if (!contacts.isEmpty){
                    for (contact in contacts) {
                        contactSms.add(contact.toObject(Contact::class.java))
                    }
                }

                smsContacts.value = contactSms
                Log.d(TAG, "fetchContacts: Success")
            }
            .addOnFailureListener {
                Log.e(TAG, "fetchContacts: Failure", it.cause)
            }
    }

    companion object {
        private val TAG = AppMessagesViewModel::class.java.simpleName
    }
}