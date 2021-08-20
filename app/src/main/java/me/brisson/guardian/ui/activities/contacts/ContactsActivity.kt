package me.brisson.guardian.ui.activities.contacts

import android.Manifest
import android.app.SearchManager
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.R
import me.brisson.guardian.data.model.Contact
import me.brisson.guardian.databinding.ActivityContactsBinding
import me.brisson.guardian.ui.adapters.ContactAdapter
import me.brisson.guardian.ui.base.BaseActivity
import me.brisson.guardian.utils.AlertHelper

/*
    todo()
        - not show user own profile.
        - only finding when name is the exact as query
        - phone contact list is doubled
        - differentiate sms contact from app contacts
*/

@AndroidEntryPoint
class ContactsActivity : BaseActivity() {

    private lateinit var binding: ActivityContactsBinding
    private val viewModel: ContactsViewModel by viewModels()
    private val adapter = ContactAdapter()

    private var extraStringValue: String? = null

    private var db = Firebase.firestore
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contacts)

        binding.viewModel = viewModel

        extraStringValue = intent?.extras?.getString(CONTACT)

        setUpAppbar()
        setUpUI()

    }

    private fun setUpUI() {
        //Checking to see if the bundle passed is sms or app contacts.
        when (extraStringValue) {
            SMS_CONTACTS -> {
                if (checkReadContactPermission()) {
                    binding.allowContactsButton.visibility = View.GONE

                    getPhoneContactList()

                    viewModel.getContacts().observe(this, {
                        if (it.isNotEmpty()) {
                            adapter.addData(it)
                            binding.noContactsPlaceholderLayout.visibility = View.GONE
                        } else {
                            binding.noContactsPlaceholderLayout.visibility = View.VISIBLE
                        }
                    })
                    setupAdapter()

                } else {
                    binding.allowContactsButton.visibility = View.VISIBLE
                    binding.noContactsPlaceholder.visibility = View.GONE
                }

                binding.allowContactsButton.setOnClickListener {
                    askForReadContactPermission()
                }

                Log.d(TAG, "ExtraStringValue: $SMS_CONTACTS")
            }
            APP_CONTACTS -> {
                binding.noContactsPlaceholderLayout.visibility = View.GONE

                viewModel.getContacts().observe(this, {
                    if (it.isNotEmpty()) {
                        adapter.addData(it)
                    }
                })

                setupAdapter()

                Log.d(TAG, "ExtraStringValue: $APP_CONTACTS")
            }
        }

    }

    // Checking if user already has contacts added and show dialog
    private fun handleContact(contact: Contact) {
        val userReference = db.collection("users").document(user!!.uid)
        val contactsCollection = userReference.collection("contacts")

        // Check if the user is already a guardian
        contactsCollection.document(contact.uid)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    Toast.makeText(this, R.string.guardian_already_added, Toast.LENGTH_SHORT).show()
                } else {
                    // Show dialog
                    AlertHelper.alertTwoButtonsDialog(
                        this,
                        getString(R.string.add_as_guardian, contact.name),
                        getString(R.string.yes),
                        getString(R.string.no),
                        { _, _ -> addGuardian(contactsCollection, contact) },
                        { _, _ ->  }

                    )
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "addGuardian: ", it)
            }

    }

    // Adding contact to user contacts collection
    private fun addGuardian(
        contactsCollection: CollectionReference,
        contact: Contact
    ) {
        contactsCollection
            .document(contact.uid)
            .set(contact)
            .addOnSuccessListener {
                Toast.makeText(this, R.string.guardian_added, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Adding contacts: Successfully.")
            }
            .addOnFailureListener {
                Toast.makeText(this, "There was an error", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Adding contacts: ", it.cause)
            }
    }

    // Set up the adapter and recycler view
    private fun setupAdapter() {
        adapter.onAddGuardianClickListener = { contact ->
            handleContact(contact)
        }

        binding.recycler.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.recycler.adapter = adapter
    }

    // Handling AppBar (NavigationClick and SearchView)
    private fun setUpAppbar() {
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        (binding.topAppBar.menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            when (extraStringValue) {
                SMS_CONTACTS -> {
                    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextChange(query: String?): Boolean {
                            if (checkReadContactPermission()) {
                                adapter.filter.filter(query)
                            }
                            return true
                        }

                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return false
                        }
                    })
                }
                APP_CONTACTS -> {
                    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextChange(query: String?): Boolean {
                            return true
                        }

                        override fun onQueryTextSubmit(query: String?): Boolean {
                            getAppUserList(query)
                            return false
                        }
                    })
                }
            }


        }

    }

    // Getting the user on firebase with a query
    private fun getAppUserList(queryValue: String?) {
        if (!queryValue.isNullOrEmpty()) {
            val usersRef = db.collection("users")
            val query = usersRef.whereEqualTo("name", queryValue)

            query.get()
                .addOnSuccessListener { users ->
                    val contacts = ArrayList<Contact>()

                    for (user in users) {
                        contacts.add(
                            Contact(
                                uid = user.getString("uid")!!,
                                name = user.getString("name")!!,
                                photo = user.getString("userImage")
                            )
                        )

                        Log.d(TAG, "$user.id ${user.data}")
                    }
                    viewModel.setContacts(sortListInAlphabeticalOrder(contacts))
                }
                .addOnFailureListener {
                    Log.e(TAG, "setUpUI: ", it.cause)
                }
        }
    }

    // Getting all user's phone contacts
    private fun getPhoneContactList() {
        val cr: ContentResolver = this.contentResolver
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

                        contacts.add(
                            Contact(
                                uid = id,
                                name = name,
                                phoneNo = phoneNo,
                                photo = photo
                            )
                        )

                    }

                    pCur.close()
                }
            }
        }
        viewModel.setContacts(sortListInAlphabeticalOrder(contacts))

        cur?.close()
    }

    // Sorting the list of contacts in alphabetical order
    private fun sortListInAlphabeticalOrder(list: List<Contact>): List<Contact> {
        return list.sortedBy { it.name }
    }

    // Asking for read phone contact
    private fun askForReadContactPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CONTACTS),
            READ_CONTACT_REQUEST_CODE
        )
    }

    // Checking if has permission to read phone contact
    private fun checkReadContactPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        )

        return result == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            READ_CONTACT_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    setUpUI()

                    makeSnackBar(
                        binding.root,
                        getString(R.string.permission_read_contacts_granted)
                    )
                } else {
                    makeActionSnackBar(
                        binding.root,
                        getString(R.string.permission_read_contacts_denied),
                        getString(R.string.retry),
                        ::askForReadContactPermission
                    )
                }
                return
            }
        }
    }

    companion object {
        private const val READ_CONTACT_REQUEST_CODE = 1

        private val TAG = ContactsActivity::javaClass.name

        // The APP_CONTACTS is a reference to the firebase contacts, and the SMS_CONTACTS is to the phone contacts.
        const val CONTACT = "contact"
        const val APP_CONTACTS = "app"
        const val SMS_CONTACTS = "sms"
    }

}