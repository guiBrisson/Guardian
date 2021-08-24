package me.brisson.guardian.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import me.brisson.guardian.R
import me.brisson.guardian.data.model.Contact
import me.brisson.guardian.databinding.ItemContactMessageBinding

class ContactMessageAdapter(
   private val messages: ArrayList<Contact>
) : RecyclerView.Adapter<ContactMessageAdapter.ViewHolder>(){
    lateinit var onItemClickListener: (item: Contact) -> Unit?

    class ViewHolder(
        private val binding: ItemContactMessageBinding,
        private val onItemClickListener: (item: Contact) -> Unit?
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Contact){

            if (item.photo!!.isNotEmpty()) {
                Picasso.get()
                    .load(item.photo)
                    .centerCrop()
                    .fit()
                    .into(binding.image)
            }else {
                Picasso.get()
                    .load(R.drawable.person_placeholder)
                    .centerCrop()
                    .fit()
                    .into(binding.image)
            }


            if (item.isGuardian){
                binding.guardianIcon.visibility = View.VISIBLE
            } else {
                binding.guardianIcon.visibility = View.GONE
            }

            binding.nameTextField.text = item.name
            binding.longAgoTextView.text = item.lastMessageTimer
            binding.messageText.text = item.lastMessage

            when(item.newMessagesCount){
                0 -> binding.messagesCountLayout.visibility = View.GONE
                else -> {
                    binding.messagesCountLayout.visibility = View.VISIBLE
                    if(item.newMessagesCount!! > 9){
                        "9+".also { binding.recentMessagesCount.text = it }
                    }else {
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
        return holder.bind(messages[position])
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun addData(items: List<Contact>){
        messages.addAll(items)
        notifyDataSetChanged()
    }
}