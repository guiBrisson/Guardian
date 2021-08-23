package me.brisson.guardian.ui.activities.contacts

import android.app.Application
import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
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
class ContactsViewModel @Inject constructor(private val application: Application) :
    BaseViewModel() {
    private val contacts = MutableLiveData<List<Contact>>()
    private val clickedContact = MutableLiveData<Contact>()

    private var db = Firebase.firestore
    private val user = FirebaseAuth.getInstance().currentUser
    private val userReference = db.collection("users").document(user!!.uid)
    private val contactsCollection = userReference.collection("contacts")

    private val addContactSuccessListener = MutableLiveData<Boolean>()
    private val isContactAdded = MutableLiveData<Boolean>()

    private var extraStringValue: String? = null

    fun getContacts(): LiveData<List<Contact>> = contacts
    fun getClickedContact(): LiveData<Contact> = clickedContact

    fun getAddContactSuccessListener(): LiveData<Boolean> = addContactSuccessListener
    fun getIsContactAdded(): LiveData<Boolean> = isContactAdded

    fun getExtraStringValue(): String? = extraStringValue
    fun setExtraStringValue(string: String?) {
        extraStringValue = string
    }

    private fun setContacts(contacts: List<Contact>) {
        this.contacts.postValue(contacts)
    }

    private fun clearContacts() {
        contacts.value = emptyList()
    }

    // Getting all users on firebase
    fun getAppUserList() {
        clearContacts()

        val usersRef = db.collection("users")

        // Getting all users
        val allUsers = ArrayList<Contact>()
        usersRef
            .get()
            .addOnSuccessListener { users ->
                for (user in users) {
                    allUsers.add(
                        Contact(
                            uid = user.getString("uid")!!,
                            name = user.getString("name")!!,
                            photo = user.getString("userImage")
                        )
                    )
                }
                // Removing user's own profile from the list
                val loggedUser = allUsers.find { contact -> contact.uid == user!!.uid }
                allUsers.remove(loggedUser)
                setContacts(allUsers)

                Log.d(TAG, "getAppUserList: Success")
            }
            .addOnFailureListener {
                Log.e(TAG, "getAppUserList: ", it.cause)
            }


    }

    // Getting all user's phone contacts
    fun getPhoneContactList() {
        val cr: ContentResolver = application.contentResolver
        val cur: Cursor? = cr.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )

        var id: String
        var name: String
        var photo: String?
        var phoneNo: String

        val contacts = ArrayList<Contact>()

        if ((cur?.count ?: 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                id = cur.getString(
                    cur.getColumnIndex(ContactsContract.Contacts._ID)
                )
                name = cur.getString(
                    cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                    )
                )

                photo = cur.getString(
                    cur.getColumnIndex(
                        ContactsContract.CommonDataKinds.Photo.PHOTO_URI
                    )
                )

                if (cur.getInt(
                        cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER
                        )
                    ) > 0
                ) {
                    val pCur: Cursor? = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    while (pCur!!.moveToNext()) {
                        phoneNo = pCur.getString(
                            pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                        )
                        val contact = Contact(
                            uid = id,
                            name = name,
                            phoneNo = phoneNo,
                            photo = photo
                        )

                        // Avoiding duplicate
                        val duplicate = contacts.filter { it.uid == contact.uid }
                        if (duplicate.isEmpty()) {
                            contacts.add(contact)
                        }

                    }

                    pCur.close()
                }
            }
        }
        setContacts(sortListInAlphabeticalOrder(contacts))

        cur?.close()
    }

    // Handling contacts after add clicked
    fun handleContact(contact: Contact) {
        // Saving clicked contact
        clickedContact.value = contact

        // Check if  contacts from phone or app
        when (extraStringValue) {
            ContactsActivity.SMS_CONTACTS -> {
                contact.isPhoneContact = true
            }
            ContactsActivity.APP_CONTACTS -> {
                contact.isPhoneContact = false
            }
        }
        // Check if the user is already a guardian
        contactsCollection.document(contact.uid)
            .get()
            .addOnSuccessListener {
                isContactAdded.value = it.exists()
                Log.d(TAG, "Add contact: Successfully")
            }
            .addOnFailureListener {
                Log.e(TAG, "Add contact: ", it)
            }

    }

    // Adding contact to user's contacts collection
    fun addContact(contact: Contact) {
        contactsCollection
            .document(contact.uid)
            .set(contact)
            .addOnSuccessListener {
                addContactSuccessListener.value = true
                Log.d(TAG, "Adding contacts: Successfully.")
            }
            .addOnFailureListener {
                addContactSuccessListener.value = false
                Log.e(TAG, "Adding contacts: ", it.cause)
            }
    }

    // Sorting the list of contacts in alphabetical order
    private fun sortListInAlphabeticalOrder(list: List<Contact>): List<Contact> {
        return list.sortedBy { it.name }
    }

    companion object {
        private val TAG = ContactsViewModel::class.java.simpleName
    }

}