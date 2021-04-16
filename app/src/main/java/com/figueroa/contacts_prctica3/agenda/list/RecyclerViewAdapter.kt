package com.figueroa.contacts_prctica3.agenda.list

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.figueroa.contacts_prctica3.R
import com.figueroa.contacts_prctica3.agenda.form.AppointmentFormFragment
import com.figueroa.contacts_prctica3.db.AgendaDB
import com.figueroa.contacts_prctica3.db.ContactsDB
import com.figueroa.contacts_prctica3.db.Operation
import com.figueroa.contacts_prctica3.models.Appointment

class RecyclerViewAdapter(private var appointment: MutableList<Appointment>,
                          private var parentFragment: AgendaAppointmentsList) :
        RecyclerView.Adapter<RecyclerViewAdapter.AppointmentsViewHolder>() {

    private var context: Context? = null

    override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int): AppointmentsViewHolder {
        val viewItem: View
        var appointmentViewHolder: AppointmentsViewHolder? = null
        try {
            viewItem = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_appointment, parent, false)
            context = parent.context
            appointmentViewHolder = AppointmentsViewHolder(viewItem, parentFragment)
        } catch (e: Exception) {
            Log.e("RECYCLERVIEW_ERROR", e.message!!)
        }
        return appointmentViewHolder!!
    }

    override fun onBindViewHolder(
            holder: AppointmentsViewHolder, position: Int) {
        val item = appointment[position]
        holder.bindContacts(item)
    }

    override fun getItemCount(): Int {
        return appointment.size
    }

    class AppointmentsViewHolder(
            itemView: View,
            private val parentFragment: AgendaAppointmentsList
    ) :
            RecyclerView.ViewHolder(itemView),
            View.OnClickListener,
            PopupMenu.OnMenuItemClickListener {

        private var appointment: Appointment? = null

        private val title: TextView =
                itemView.findViewById(R.id.recyclerview_appointment_title_text)
        private val dateTime: TextView =
                itemView.findViewById(R.id.recyclerview_appointment_date_text)
        private val with: TextView =
                itemView.findViewById(R.id.recyclerview_appointment_with_text)
        private val editButton: ImageButton =
                itemView.findViewById(R.id.recyclerview_appointment_options_button)

        fun bindContacts(appointment: Appointment?) {
            this.appointment = appointment
            title.text = appointment?.title ?: "Unknown appointment title"
            dateTime.text = appointment?.dateTime ?: "Unknown appointment date"
            with.text = ContactsDB(parentFragment.requireContext()).getContactById(
                    appointment?.with!!,
                    arrayOf("_id", "name", "phoneNumber", "about")
            )?.name ?: "Unknown appointment date"

            editButton.setOnClickListener(this)
            if (appointment.id == -1) editButton.isEnabled = false
        }

        override fun onClick(v: View?) {
            val popupMenu = PopupMenu(parentFragment.requireContext(), v)
            val inflater: MenuInflater = popupMenu.menuInflater

            inflater.inflate(R.menu.appointment_options_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.show()
        }

        override fun onMenuItemClick(item: MenuItem?): Boolean {
            when(item?.itemId) {
                R.id.menu_edit_appointment -> {
                    AppointmentFormFragment(
                            operation = Operation.UPDATE,
                            appointment = appointment
                    ).show(parentFragment.childFragmentManager, "edit_appointment")
                }
                R.id.menu_delete_appointment -> {
                    val alert = AlertDialog
                            .Builder(parentFragment.requireContext())

                    alert.setTitle("Delete contact")
                    alert.setMessage("Are you sure you want to delete this appointment?")
                    alert.setNegativeButton("Cancel", null)
                    alert.setPositiveButton("Delete"
                    ) { dialog, _ ->
                        AgendaDB(parentFragment.requireContext()).deleteAppointment(appointment = appointment)
                        dialog.dismiss()
                    }

                    alert.setOnDismissListener {
                         parentFragment.updateRecyclerView()
                    }

                    alert.show()
                }
            }
            return false
        }
    }
}

