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
import me.brisson.guardian.ui.activities.contacts.ContactsActivity
import me.brisson.guardian.ui.base.BaseFragment
import java.io.Serializable


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
                startActivity(ContactsActivity())
            }

        }

        return binding.root
    }

    private fun askForReadContactPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_CONTACTS),
            1
        )
    }

    private fun checkReadContactPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_CONTACTS
        )
        return result == PackageManager.PERMISSION_GRANTED
    }


}