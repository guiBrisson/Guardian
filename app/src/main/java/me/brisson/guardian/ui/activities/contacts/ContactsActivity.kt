package me.brisson.guardian.ui.activities.contacts

import android.app.SearchManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
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

    private lateinit var adapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contacts)

        binding.viewModel = viewModel
        
        setupAppbar()
        getContactList()
        setupUI()

        handleIntent(intent!!)
    }

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
                    ) > 0) {
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
                                id = id,
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

    private fun sortListInAlphabeticalOrder(list: List<Contact>): List<Contact>{
        return list.sortedBy { it.name }
    }

    private fun setupUI (){
        adapter = ContactAdapter(arrayListOf())

        viewModel.getContacts().observe(this, Observer {
            if (it.isNotEmpty()) {
                adapter.addData(it)
            }
        })
        adapter.onAddGuardianClickListener = {  }

        binding.recycler.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.recycler.adapter = adapter

        binding.fastScroll.setRecyclerView(binding.recycler)

    }
    
    private fun setupAppbar(){
        binding.topAppBar.menu.removeItem(R.id.notifications)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }
        return true
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent!!)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)

        }
    }



}