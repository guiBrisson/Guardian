package me.brisson.guardian.ui.fragments.appmessages

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import me.brisson.guardian.data.model.Contact
import me.brisson.guardian.data.model.User
import me.brisson.guardian.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class AppMessagesViewModel @Inject constructor() : BaseViewModel() {
    private val appContacts = MutableLiveData<ArrayList<Contact>>()
    fun getContacts(): LiveData<ArrayList<Contact>> = appContacts

    private val user = FirebaseAuth.getInstance().currentUser

    private val db = Firebase.firestore
    private val usersRef = db.collection("users")
    private val contactRef = usersRef.document(user!!.uid).collection("contacts")

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

                if (!contacts.isEmpty) {
                    for (contact in contacts) {
                        val c = contact.toObject(Contact::class.java)
                        fetchUserContact(c)
                        contactApp.add(c)

                    }
                }
                appContacts.value = contactApp
                Log.d(TAG, "fetchContacts: Success")
            }
            .addOnFailureListener {
                Log.e(TAG, "fetchContacts: Failure", it.cause)
            }
    }

    // TODO: FIX THIS
    private fun fetchUserContact(c: Contact) {
        usersRef
            .document(c.uid)
            .get()
            .addOnSuccessListener { user ->
                if (user.exists()) {
                    val u = user.toObject(User::class.java)!!
                    c.name = u.name
                    c.photo = u.userImage

                    updateContact(c)
                }

            }
            .addOnFailureListener {
                Log.e(TAG, "fetchUserContact: Failure", it.cause)
            }
    }


    private fun updateContact(contact: Contact){
        contactRef
            .document(contact.uid)
            .apply {
                update("name", contact.name)
                    .addOnSuccessListener {  }
                    .addOnFailureListener {
                        Log.e(TAG, "updateContactName: ", it.cause)
                    }
                update("photo", contact.photo)
                    .addOnSuccessListener {  }
                    .addOnFailureListener {
                        Log.e(TAG, "updateContactPhoto: ", it.cause)
                    }
            }
    }

    companion object {
        private val TAG = AppMessagesViewModel::class.java.simpleName
    }

}