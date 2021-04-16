package com.figueroa.contacts_prctica3.contacts.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.figueroa.contacts_prctica3.R
import com.figueroa.contacts_prctica3.db.ContactsDB
import com.figueroa.contacts_prctica3.models.Contact
import java.lang.Exception

class ContactsFragment : Fragment() {

    // DB
    private var db: ContactsDB? = null
    // Recyclerview
    private var contacts: RecyclerView? = null
    private var adapter: RecyclerViewAdapter? = null
    private var data: MutableList<Contact>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getRecyclerView()
    }

    private fun getData() {
        try {
            db = ContactsDB(context!!.applicationContext)
            data = db!!.getAllContactsSimple("name")
            if (data.isNullOrEmpty())
                data = mutableListOf(Contact(-1, "Oops!",
                        "Could not retrieve contacts...",
                        "We are sorry to tell you that we could not retrieve the " +
                                "list of contacts 😖"))
        } catch (exception: Exception) {
            data = mutableListOf(Contact(1, "Oops!", "Could not retrieve contacts...",  ""))
            Toast.makeText(context?.applicationContext,
                "Could not retrieve contacts",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun getRecyclerView() {
        try {
            contacts = view!!.findViewById(R.id.contacts_recyclerview)
            adapter = RecyclerViewAdapter(data!!)
            contacts!!.adapter = adapter
            contacts!!.layoutManager = LinearLayoutManager(context!!.applicationContext)
        } catch (exception: Exception) {
            Toast.makeText(context?.applicationContext,
                "Could not create list",
                Toast.LENGTH_SHORT).show()
        }
    }
}