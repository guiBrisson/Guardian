package me.brisson.guardian.ui.activities.contacts

import android.Manifest
import android.app.SearchManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.R
import me.brisson.guardian.databinding.ActivityContactsBinding
import me.brisson.guardian.ui.adapters.ContactAdapter
import me.brisson.guardian.ui.base.BaseActivity
import me.brisson.guardian.utils.AlertHelper

@AndroidEntryPoint
class ContactsActivity : BaseActivity() {

    private lateinit var binding: ActivityContactsBinding
    private val viewModel: ContactsViewModel by viewModels()
    private val adapter = ContactAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contacts)

        binding.viewModel = viewModel

        viewModel.setExtraStringValue(intent?.extras?.getString(CONTACT))

        setUpAppbar()
        setUpUI()

    }

    // Setting up the user interface
    private fun setUpUI() {
        //Checking to see if the bundle passed is sms or app contacts.
        when (viewModel.getExtraStringValue()) {
            SMS_CONTACTS -> {
                if (checkReadContactPermission()) {
                    binding.allowContactsButton.visibility = View.GONE

                    viewModel.getPhoneContactList()

                    viewModel.getContacts().observe(this, {
                        if (it.isNotEmpty()) {
                            adapter.addData(it)
                            setupAdapter()
                            binding.noContactsPlaceholderLayout.visibility = View.GONE
                        } else {
                            binding.noContactsPlaceholder.text =
                                getString(R.string.contacts_placeholder)
                            binding.noContactsPlaceholderLayout.visibility = View.VISIBLE
                        }
                    })

                } else {
                    binding.allowContactsButton.visibility = View.VISIBLE
                    binding.noContactsPlaceholderLayout.visibility = View.GONE
                }

                binding.allowContactsButton.setOnClickListener {
                    askForReadContactPermission()
                }

                Log.d(TAG, "ExtraStringValue: $SMS_CONTACTS")
            }
            APP_CONTACTS -> {
                binding.noContactsPlaceholderLayout.visibility = View.GONE

                viewModel.getAppUserList()

                viewModel.getContacts().observe(this, {
                    if (it.isNotEmpty()) {
                        adapter.addData(it)
                        setupAdapter()
                        binding.noContactsPlaceholderLayout.visibility = View.GONE
                    } else {
                        binding.noContactsPlaceholder.text = getString(R.string.no_contacts_found)
                        binding.noContactsPlaceholderLayout.visibility = View.VISIBLE
                    }
                })


                Log.d(TAG, "ExtraStringValue: $APP_CONTACTS")
            }
        }

        viewModel.getAddContactSuccessListener().observe(this, {
            if (it){
                Toast.makeText(this, R.string.contact_added, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "There was an error", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.getIsContactAdded().observe(this, {
            if (it){
                Toast.makeText(this, R.string.contact_already_added, Toast.LENGTH_SHORT).show()
            } else {
                // Show dialog
                AlertHelper.alertTwoButtonsDialog(
                    this,
                    getString(R.string.add_as_contact, viewModel.getClickedContact().value!!.name),
                    getString(R.string.yes),
                    getString(R.string.no),
                    { _, _ -> viewModel.addContact(viewModel.getClickedContact().value!!) },
                    { _, _ -> }
                )}
        })


    }

    // Set up the adapter and recycler view
    private fun setupAdapter() {
        adapter.onAddGuardianClickListener = { contact ->
            viewModel.handleContact(contact)
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
            when (viewModel.getExtraStringValue()) {
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
                            adapter.filter.filter(query)
                            return true
                        }

                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return false
                        }
                    })
                }
            }


        }

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

        private val TAG = ContactsActivity::class.java.simpleName

        // The APP_CONTACTS is a reference to the firebase contacts, and the SMS_CONTACTS is to the phone contacts.
        const val CONTACT = "contact"
        const val APP_CONTACTS = "app"
        const val SMS_CONTACTS = "sms"
    }

}