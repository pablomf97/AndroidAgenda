package com.figueroa.contacts_prctica3.agenda

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.figueroa.contacts_prctica3.R
import com.figueroa.contacts_prctica3.agenda.list.AgendaAppointmentsList
import com.figueroa.contacts_prctica3.db.AgendaDB
import com.figueroa.contacts_prctica3.models.Constants
import java.util.*

class AgendaFragment :
        Fragment(),
        CalendarView.OnDateChangeListener {

    var calendar: CalendarView? = null

    var agendaDB: AgendaDB? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_agenda, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        agendaDB = AgendaDB(requireContext())

        calendar = view.findViewById(R.id.agenda_calendar)
        calendar?.setOnDateChangeListener(this)
        calendar?.minDate = Date().toInstant().toEpochMilli()

    }

    override fun onSelectedDayChange(view: CalendarView, year: Int, month: Int, dayOfMonth: Int) {
        val query = "$dayOfMonth/${Constants.monthsNumber[month]}/${year}"
        val appointments = agendaDB?.findByDate(query)
        if (appointments?.isNullOrEmpty() == false) {
            val xlarge = context!!.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_XLARGE
            val large = context!!.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_LARGE

            if (large || xlarge) {
                val transaction: FragmentTransaction = (context as? FragmentActivity)!!.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frameLayout_land, AgendaAppointmentsList(query))
                transaction.commit()
            } else if (context?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                val transaction: FragmentTransaction = (context as? FragmentActivity)!!.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frameLayout_land, AgendaAppointmentsList(query))
                transaction.commit()
            } else if (context?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
                AgendaAppointmentsList(query).show(
                        (context as? FragmentActivity)!!.supportFragmentManager,
                        "list_appointment"
                )
            }

        } else {
            Toast.makeText(
                    requireContext(),
                    "No appointments scheduled for this day...",
                    Toast.LENGTH_SHORT
            ).show()
        }
    }



}