package me.brisson.guardian.ui.fragments.appmessages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.databinding.FragmentAppMessagesBinding
import me.brisson.guardian.ui.activities.chat.ChatActivity
import me.brisson.guardian.ui.activities.contacts.ContactsActivity
import me.brisson.guardian.ui.adapters.ContactMessageAdapter
import me.brisson.guardian.ui.base.BaseFragment

@AndroidEntryPoint
class AppMessagesFragment : BaseFragment() {

    companion object {
        fun newInstance() = AppMessagesFragment()
    }

    private lateinit var binding: FragmentAppMessagesBinding
    private val viewModel = AppMessagesViewModel()
    private lateinit var adapterContact: ContactMessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppMessagesBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        setUpRecycler()
        handleClicks()

        return binding.root
    }

    private fun handleClicks() {
        binding.addGuardianButton.setOnClickListener {
            // Passing the extra APP_CONTACTS, since ContactsActivity is used for both sms and app contacts.
            val mIntent = Intent(activity, ContactsActivity::class.java)
            mIntent.putExtra(ContactsActivity.CONTACT, ContactsActivity.APP_CONTACTS)
            startActivity(mIntent)
        }
    }

    private fun setUpRecycler() {
        adapterContact = ContactMessageAdapter(arrayListOf())
        binding.recycler.adapter = adapterContact
        binding.recycler.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        viewModel.getContacts().observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                adapterContact.addData(it)
            }
        })

        adapterContact.onItemClickListener = { contact ->
            // Sending contact to ChatActivity as serializable
            val intent = Intent(requireActivity(), ChatActivity::class.java)
            intent.putExtra("contact", contact)
            startActivity(intent)

        }
    }


}