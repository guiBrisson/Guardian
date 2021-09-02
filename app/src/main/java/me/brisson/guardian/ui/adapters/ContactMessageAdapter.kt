package me.brisson.guardian.ui.adapters

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import me.brisson.guardian.R
import me.brisson.guardian.data.model.Contact
import me.brisson.guardian.data.model.User
import me.brisson.guardian.databinding.ItemContactMessageBinding
import kotlin.collections.ArrayList

class ContactMessageAdapter(
    private val contacts: ArrayList<Contact>
) : RecyclerView.Adapter<ContactMessageAdapter.ViewHolder>() {
    lateinit var onItemClickListener: (item: Contact) -> Unit?

    class ViewHolder(
        private val binding: ItemContactMessageBinding,
        private val onItemClickListener: (item: Contact) -> Unit?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Contact) {

            // Placing a place holder if contact has no photo
            if (item.photo!!.isNotEmpty()) {
                Picasso.get()
                    .load(item.photo)
                    .centerCrop()
                    .fit()
                    .into(binding.image)
            } else {
                Picasso.get()
                    .load(R.drawable.person_placeholder)
                    .centerCrop()
                    .fit()
                    .into(binding.image)
            }

            if (item.isGuardian) {
                binding.guardianIcon.visibility = View.VISIBLE
            } else {
                binding.guardianIcon.visibility = View.GONE
            }

            binding.nameTextField.text = item.name

            // Checking if there is a last message and binding the time stamp and the message
            if (!item.lastMessage.isNullOrEmpty()) {
                val currentTimeMillis = System.currentTimeMillis()
                val relativeTimeSpan = DateUtils.getRelativeTimeSpanString(
                    item.lastMessageTimer!!,
                    currentTimeMillis,
                    DateUtils.MINUTE_IN_MILLIS
                )
                binding.longAgoTextView.text = relativeTimeSpan

                binding.messageText.text = item.lastMessage
            }

            // todo?
            when (item.newMessagesCount) {
                0 -> binding.messagesCountLayout.visibility = View.GONE
                else -> {
                    binding.messagesCountLayout.visibility = View.VISIBLE
                    if (item.newMessagesCount!! > 9) {
                        "9+".also { binding.recentMessagesCount.text = it }
                    } else {
                        binding.recentMessagesCount.text = item.newMessagesCount.toString()
                    }
                }
            }

            binding.root.setOnClickListener {
                onItemClickListener.invoke(item)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemContactMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), onItemClickListener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(contacts[position])
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    fun addData(items: List<Contact>) {
        contacts.addAll(items)
        notifyDataSetChanged()
    }
}