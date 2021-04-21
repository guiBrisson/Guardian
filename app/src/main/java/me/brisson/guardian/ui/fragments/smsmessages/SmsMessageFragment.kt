package me.brisson.guardian.ui.fragments.smsmessages

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.data.model.Contact
import me.brisson.guardian.databinding.FragmentSmsMessageBinding
import me.brisson.guardian.ui.base.BaseFragment


@AndroidEntryPoint
class SmsMessageFragment : BaseFragment() {

    companion object {
        fun newInstance() = SmsMessageFragment()
    }

    private lateinit var binding: FragmentSmsMessageBinding
    private val viewModel = SmsMessageViewModel()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentSmsMessageBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel


        binding.addGuardianTextView.setOnClickListener {
            if (!checkReadContactPermission()) {
                askForReadContactPermission()
            } else {
                getContactList()
            }

        }

        viewModel.getContacts().observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {

            }
        })

        return binding.root
    }

    private fun getContactList() {
        showDialog()
        val cr: ContentResolver = requireActivity().contentResolver
        val cur: Cursor? = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null)

        var id: String
        var name: String
        var photo: String?
        var phoneNo: String

        val contacts = ArrayList<Contact>()

        if ((cur?.count ?: 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID))
                name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME))

                photo = cur.getString(cur.getColumnIndex(
                        ContactsContract.CommonDataKinds.Photo.PHOTO_URI))

                if (cur.getInt(cur.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    val pCur: Cursor? = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id), null)
                    while (pCur!!.moveToNext()) {
                        phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER))

                        contacts.add(Contact(
                                id = id,
                                name = name,
                                phoneNo = phoneNo,
                                photo = photo
                        ))

                    }

                    pCur.close()
                }
            }
        }
        viewModel.addContacts(contacts)
        hideDialog()
        cur?.close()
    }

    private fun askForReadContactPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.READ_CONTACTS),
                1)
    }

    private fun checkReadContactPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
        return result == PackageManager.PERMISSION_GRANTED
    }


}