package me.brisson.guardian.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import me.brisson.guardian.data.model.Message
import me.brisson.guardian.databinding.ItemFromMessageBinding
import me.brisson.guardian.databinding.ItemToMessageBinding

// TODO() handle timeStamp
class MessageAdapter(
    private val messages: ArrayList<Message>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    class ViewHolder(private val binding: ItemFromMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Message) {
            binding.messageText.text = item.message
        }
    }

    class ViewHolder1(private val binding: ItemToMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Message) {
            binding.messageText.text = item.message
        }
    }

    fun addData(items: List<Message>) {
        messages.addAll(items)
        notifyDataSetChanged()
    }


    override fun getItemViewType(position: Int): Int {
        return if (messages[position].fromId == FirebaseAuth.getInstance().currentUser?.uid) {
            VIEW_TYPE_ONE
        } else {
            VIEW_TYPE_ZERO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ZERO) {
            ViewHolder(
                ItemFromMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            ViewHolder1(
                ItemToMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (messages[position].fromId == FirebaseAuth.getInstance().currentUser?.uid) {
            (holder as ViewHolder1).bind(messages[position])
        } else {
            (holder as ViewHolder).bind(messages[position])
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    companion object {
        const val VIEW_TYPE_ZERO = 0
        const val VIEW_TYPE_ONE = 1
    }

}