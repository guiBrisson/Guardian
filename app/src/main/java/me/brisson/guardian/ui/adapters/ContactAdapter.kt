package me.brisson.guardian.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import me.brisson.guardian.R
import me.brisson.guardian.data.model.Contact
import me.brisson.guardian.databinding.ItemContactBinding
import java.util.*
import kotlin.collections.ArrayList

class ContactAdapter :
        RecyclerView.Adapter<ContactAdapter.ViewHolder>(),
        Filterable {

    private val contacts: ArrayList<Contact> = arrayListOf()
    private val contactsFiltered: ArrayList<Contact> = arrayListOf()

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
        contactsFiltered.addAll(items)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return contactFilter
    }

    private val contactFilter = (object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList: ArrayList<Contact> = arrayListOf()

            if (constraint.isNullOrEmpty()) {
                filteredList.addAll(contactsFiltered)
            } else {
                val filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim()

                for (item in contactsFiltered) {
                    if (item.name.toLowerCase(Locale.ROOT).contains(filterPattern) || item.phoneNo.toLowerCase(Locale.ROOT).contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            with(contacts) {
                clear()
                addAll(results?.values as List<Contact>)
            }
            notifyDataSetChanged()
        }
    })


}