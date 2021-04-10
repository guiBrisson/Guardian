package me.brisson.guardian.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import me.brisson.guardian.R
import me.brisson.guardian.data.model.Message
import me.brisson.guardian.databinding.ItemMessageBinding

class MessageAdapter(
   private val messages: ArrayList<Message>
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>(){
    lateinit var onItemClickListener: (item: Message) -> Unit?

    class ViewHolder(
        private val binding: ItemMessageBinding,
        private val onItemClickListener: (item: Message) -> Unit?
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Message){

            if (item.image.isNotEmpty()) {
                Picasso.get()
                    .load(item.image)
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
            binding.longAgoTextView.text = item.timeAgo
            binding.messageText.text = item.message

            when(item.newMessages){
                0 -> binding.messagesCountLayout.visibility = View.GONE
                else -> {
                    binding.messagesCountLayout.visibility = View.VISIBLE
                    if(item.newMessages > 9){
                        "9+".also { binding.recentMessagesCount.text = it }
                    }else {
                        binding.recentMessagesCount.text = item.newMessages.toString()
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
            ItemMessageBinding.inflate(
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

    fun addData(items: List<Message>){
        messages.addAll(items)
        notifyDataSetChanged()
    }
}