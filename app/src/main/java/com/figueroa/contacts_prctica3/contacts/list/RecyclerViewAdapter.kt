package com.figueroa.contacts_prctica3.contacts.list

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.res.Configuration
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.figueroa.contacts_prctica3.R
import com.figueroa.contacts_prctica3.contacts.details.ContactDetailsDialogFragment
import com.figueroa.contacts_prctica3.contacts.form.ContactFormFragment
import com.figueroa.contacts_prctica3.db.Operation
import com.figueroa.contacts_prctica3.db.ContactsDB
import com.figueroa.contacts_prctica3.models.Contact


class RecyclerViewAdapter(private var contacts: MutableList<Contact>) :
        RecyclerView.Adapter<RecyclerViewAdapter.ContactsViewHolder>() {

    private var context: FragmentActivity? = null

    override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val viewItem: View
        var contactsViewHolder: ContactsViewHolder? = null
        try {
            viewItem = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_contact, parent, false)
            context = parent.context as FragmentActivity
            contactsViewHolder = ContactsViewHolder(viewItem, context!!)
        } catch (e: Exception) {
            Log.e("RECYCLERVIEW_ERROR", e.message!!)
        }
        return contactsViewHolder!!
    }

    override fun onBindViewHolder(
            holder: ContactsViewHolder, position: Int) {
        val item = contacts[position]
        setOnClickListener(holder.itemView, position)
        holder.bindContacts(item)
    }

    private fun setOnClickListener(itemView: View, position: Int) {
        itemView.setOnClickListener {
            try {
                val xlarge = context!!.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_XLARGE
                val large = context!!.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_LARGE

                if (large || xlarge) {
                    val transaction: FragmentTransaction = context?.supportFragmentManager!!.beginTransaction()
                    transaction.replace(
                            R.id.frameLayout_land,
                            ContactDetailsDialogFragment(
                                    contacts[position].id ?: -1,
                                    contacts[position].name ?: "Error!",
                                    contacts[position].phoneNumber ?: "Error!",
                                    contacts[position].about ?: "Error!"
                            )
                    )
                    transaction.commit()
                } else if (context?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    ContactDetailsDialogFragment(
                            contacts[position].id ?: -1,
                            contacts[position].name ?: "Error!",
                            contacts[position].phoneNumber ?: "Error!",
                            contacts[position].about ?: "Error!",
                    ).show(context!!.supportFragmentManager, "contact_details")
                } else if (context?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    val transaction: FragmentTransaction = context?.supportFragmentManager!!.beginTransaction()
                    transaction.replace(
                            R.id.frameLayout_land,
                            ContactDetailsDialogFragment(
                                    contacts[position].id ?: -1,
                                    contacts[position].name ?: "Error!",
                                    contacts[position].phoneNumber ?: "Error!",
                                    contacts[position].about ?: "Error!"
                            )
                    )
                    transaction.commit()
                }
            } catch (exception: java.lang.Exception) {
                println("Whoops!")
            }
        }
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    class ContactsViewHolder(
            itemView: View,
            private val context: FragmentActivity
    ) :
            RecyclerView.ViewHolder(itemView),
            View.OnClickListener,
            PopupMenu.OnMenuItemClickListener {

        private val name: TextView =
                itemView.findViewById(R.id.recyclerview_contact_name_text)
        private val phoneNumber: TextView =
                itemView.findViewById(R.id.recyclerview_contact_phone_text)
        private val editButton: ImageButton =
                itemView.findViewById(R.id.recyclerview_contact_options_button)
        private var contact: Contact? = null

        fun bindContacts(contact: Contact?) {
            this.contact = contact
            name.text = contact?.name ?: "Unknown contact name"
            phoneNumber.text = contact?.phoneNumber ?: "Unknown contact number"
            editButton.setOnClickListener(this)
            if (contact?.id == -1) editButton.isEnabled = false
        }

        override fun onClick(v: View?) {

            val popupMenu = PopupMenu(context, v)
            val inflater: MenuInflater = popupMenu.menuInflater

            inflater.inflate(R.menu.contact_options_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.show()
        }

        override fun onMenuItemClick(item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.menu_edit_contact -> {
                    ContactFormFragment(
                            operation = Operation.UPDATE,
                            contact = contact
                    ).show(context.supportFragmentManager, "edit_contact")

                    return true
                }
                R.id.menu_delete_contact -> {
                    val alert = AlertDialog
                            .Builder(context)

                    alert.setTitle("Delete contact")
                    alert.setMessage("Are you sure you want to delete this contact?")
                    alert.setNegativeButton("Cancel", null)
                    alert.setPositiveButton("Delete"
                    ) { dialog, _ ->
                        ContactsDB(context).deleteContact(contact = contact)
                        dialog.dismiss()
                    }

                    alert.setOnDismissListener {
                        val activity: Activity = context
                        if (activity is DialogInterface.OnDismissListener)
                            (activity as DialogInterface.OnDismissListener).
                            onDismiss(it)
                    }

                    alert.show()

                    return true
                }
            }
            return false
        }
    }
}

