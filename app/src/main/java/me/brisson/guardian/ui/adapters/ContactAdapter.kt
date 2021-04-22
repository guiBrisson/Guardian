package me.brisson.guardian.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider
import com.squareup.picasso.Picasso
import me.brisson.guardian.R
import me.brisson.guardian.data.model.Contact
import me.brisson.guardian.databinding.ItemContactBinding

class ContactAdapter(
    private val contacts: ArrayList<Contact>
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>(),
    SectionTitleProvider {
    lateinit var onAddGuardianClickListener: (item: Contact) -> Unit?

    class ViewHolder(
        private val binding: ItemContactBinding,
        private val onAddGuardianClickListener: (item: Contact) -> Unit?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Contact) {

            if (!item.photo.isNullOrEmpty()) {
                Picasso
                    .get()
                    .load(item.photo)
                    .fit()
                    .centerCrop()
                    .into(binding.contactImage)
            } else {
                Picasso
                    .get()
                    .load(R.drawable.person_placeholder)
                    .fit()
                    .centerCrop()
                    .into(binding.contactImage)
            }

            binding.contactName.text = item.name
            binding.contactPhoneNumber.text = item.phoneNo

            binding.addGuardian.setOnClickListener {
                onAddGuardianClickListener.invoke(item)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemContactBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), onAddGuardianClickListener
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

    override fun getSectionTitle(position: Int): String {
        //todo not working ????
        return contacts[position].name.substring(0, 1)
    }
}