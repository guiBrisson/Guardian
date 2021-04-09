package me.brisson.guardian.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import me.brisson.guardian.data.model.Notification
import me.brisson.guardian.databinding.ItemNotificationBinding

class NotificationAdapter(
    private val notifications: ArrayList<Notification>
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    lateinit var onItemClickListener: (item: Notification) -> Unit

    class ViewHolder(
        private val binding: ItemNotificationBinding,
        private val onItemClickListener: (item: Notification) -> Unit?
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Notification){

            if (item.image.isNotEmpty()){
                Picasso
                    .get()
                    .load(item.image)
                    .centerCrop()
                    .fit()
                    .into(binding.image)
            }

            binding.notificationText.text = item.text
            binding.daysText.text = item.days
            binding.dateText.text = item.date

            binding.root.setOnClickListener {
                onItemClickListener.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), onItemClickListener
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(notifications[position])

    override fun getItemCount(): Int = notifications.size

    fun addData(items: List<Notification>){
        notifications.addAll(items)
        notifyDataSetChanged()
    }
}