package me.brisson.guardian.ui.fragments.smsmessages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.databinding.FragmentSmsMessageBinding
import me.brisson.guardian.ui.activities.contacts.ContactsActivity
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
            startActivity(ContactsActivity())
        }

        return binding.root
    }






}