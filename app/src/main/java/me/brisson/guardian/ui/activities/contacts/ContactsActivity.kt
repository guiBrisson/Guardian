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
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.R
import me.brisson.guardian.data.model.Contact
import me.brisson.guardian.databinding.ActivityContactsBinding
import me.brisson.guardian.ui.adapters.ContactAdapter
import me.brisson.guardian.ui.base.BaseActivity

@AndroidEntryPoint
class ContactsActivity : BaseActivity() {

    private lateinit var binding: ActivityContactsBinding
    private val viewModel: ContactsViewModel by viewModels()
    private val adapter = ContactAdapter()
    private var extraStringValue: String? = null


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
                    binding.fab.visibility = View.VISIBLE

                    getContactList()

                    viewModel.getContacts().observe(this, {
                        if (it.isNotEmpty()) {
                            adapter.addData(it)
                            binding.noContactsPlaceholder.visibility = View.GONE
                        } else {
                            binding.fab.visibility = View.GONE
                            binding.noContactsPlaceholder.visibility = View.VISIBLE
                        }
                    })
                    setupAdapter()

                } else {
                    binding.fab.visibility = View.GONE
                    binding.allowContactsButton.visibility = View.VISIBLE
                    binding.noContactsPlaceholder.visibility = View.GONE
                }

                binding.allowContactsButton.setOnClickListener {
                    askForReadContactPermission()
                }

                Log.d(TAG, "ExtraStringValue: $SMS_CONTACTS")
            }
            APP_CONTACTS -> {
                //todo() search for all the contacts in the app.
                binding.noContactsPlaceholder.visibility = View.GONE
                Log.d(TAG, "ExtraStringValue: $APP_CONTACTS")
            }
        }

        binding.fab.setOnClickListener {
            Log.d("selectedContacts", "setUpUI: ${viewModel.getSelectedContacts()}")
            onBackPressed()
        }

    }

    private fun setupAdapter() {
        adapter.onAddGuardianClickListener = {
            viewModel.setSelectedContacts(it)
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

    }

    // Getting all users phone contacts
    private fun getContactList() {
        showDialog()
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
        hideDialog()
        cur?.close()
    }

    // Sorting the list of contacts in alphabetical order
    private fun sortListInAlphabeticalOrder(list: List<Contact>): List<Contact> {
        return list.sortedBy { it.name }
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
                    ).setAnchorView(binding.fab).show()
                } else {
                    makeActionSnackBar(
                        binding.root,
                        getString(R.string.permission_read_contacts_denied),
                        getString(R.string.retry),
                        ::askForReadContactPermission
                    ).setAnchorView(binding.fab).show()
                }
                return
            }
        }
    }

    private fun checkReadContactPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        )

        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun askForReadContactPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CONTACTS),
            READ_CONTACT_REQUEST_CODE
        )
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