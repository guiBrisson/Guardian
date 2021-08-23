package me.brisson.guardian.ui.fragments.appmessages

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import me.brisson.guardian.data.model.Contact
import me.brisson.guardian.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class AppMessagesViewModel @Inject constructor() : BaseViewModel() {
    private val appContacts = MutableLiveData<ArrayList<Contact>>()
    private val user = FirebaseAuth.getInstance().currentUser
    private val db = Firebase.firestore
    private val contactRef = db.collection("users").document(user!!.uid).collection("contacts")

    fun getContacts(): LiveData<ArrayList<Contact>> = appContacts

    init {
        fetchContacts()
    }

    private fun fetchContacts() {
        contactRef
            .whereEqualTo("isPhoneContact", false) // Filtering app contacts
            .get()
            .addOnSuccessListener { contacts ->
                // Mapping and adding app contacts
                val contactApp = ArrayList<Contact>()
                for (contact in contacts) {
                    contactApp.add(
                        Contact(
                            uid = contact.getString("uid")!!,
                            name = contact.getString("name")!!,
                            isPhoneContact = contact.getBoolean("isPhoneContact")!!,
                            photo = contact.getString("photo"),
                            phoneNo = contact.getString("phoneNo"),
                            isGuardian = contact.getBoolean("isGuardian")!!,
                            lastMessage = contact.getString("lastMessage"),
                            lastMessageTimer = contact.getString("lastMessageTimer")
                        )
                    )
                }

                appContacts.value = contactApp
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