package com.figueroa.contacts_prctica3.agenda.list

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.figueroa.contacts_prctica3.R
import com.figueroa.contacts_prctica3.agenda.list.RecyclerViewAdapter
import com.figueroa.contacts_prctica3.db.AgendaDB
import com.figueroa.contacts_prctica3.db.ContactsDB
import com.figueroa.contacts_prctica3.models.Appointment
import com.figueroa.contacts_prctica3.models.Contact
import java.lang.Exception

class AgendaAppointmentsList(
        private var query: String):
        DialogFragment() {

    private var appointmentsRecyclerView: RecyclerView? = null
    private var adapter: RecyclerViewAdapter? = null
    private var data: MutableList<Appointment>? = null

    private var agendaDB: AgendaDB? = null

    private var closeButton: ImageButton? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        getData()
        return inflater.inflate(R.layout.fragment_appointments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getRecyclerView()

        closeButton = view.findViewById(R.id.appointments_close_button)
        closeButton?.setOnClickListener { dismiss() }
    }

    private fun getData() {
        try {
            if (agendaDB == null) agendaDB = AgendaDB(context!!.applicationContext)
            data = agendaDB!!.findByDate(query)
            if (data.isNullOrEmpty())
                data = mutableListOf(Appointment(-1, "Oops!",
                        "Could not retrieve appointments...",
                        -1))
        } catch (exception: Exception) {
            data = mutableListOf(Appointment(-1, "Oops!",
                    "Could not retrieve contacts...",  -1))
            Toast.makeText(context?.applicationContext,
                    "Could not retrieve appointments",
                    Toast.LENGTH_SHORT).show()
        }
    }

    private fun getRecyclerView() {
        try {
            appointmentsRecyclerView = view!!.findViewById(R.id.appointments_recyclerview)
            adapter = RecyclerViewAdapter(data!!, this)
            appointmentsRecyclerView!!.adapter = adapter
            appointmentsRecyclerView!!.layoutManager = LinearLayoutManager(context!!.applicationContext)
        } catch (exception: Exception) {
            Toast.makeText(context?.applicationContext,
                    "Could not create list",
                    Toast.LENGTH_SHORT).show()
        }
    }

    fun updateRecyclerView() {
        getData()
        getRecyclerView()
    }
}