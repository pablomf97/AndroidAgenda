package com.figueroa.contacts_prctica3.agenda.form

import android.app.Activity
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.figueroa.contacts_prctica3.R
import com.figueroa.contacts_prctica3.db.AgendaDB
import com.figueroa.contacts_prctica3.db.ContactsDB
import com.figueroa.contacts_prctica3.db.Operation
import com.figueroa.contacts_prctica3.models.Appointment
import com.figueroa.contacts_prctica3.models.Constants
import com.figueroa.contacts_prctica3.models.Contact
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import java.lang.Exception
import java.time.YearMonth
import java.util.*

class AppointmentFormFragment(
        private val operation: Operation,
        private var appointment: Appointment?
) :
        DialogFragment(), View.OnClickListener {

    // DB
    private var contactsDB: ContactsDB? = null
    private var agendaDB: AgendaDB? = null

    // Close button
    private var closeButton: ImageButton? = null

    // Text input layouts
    private var titleInputLayout: TextInputLayout? = null
    private var monthInputLayout: TextInputLayout? = null
    private var dayInputLayout: TextInputLayout? = null
    private var yearInputLayout: TextInputLayout? = null
    private var hourInputLayout: TextInputLayout? = null
    private var minuteInputLayout: TextInputLayout? = null
    private var whoInputLayout: TextInputLayout? = null

    // Auto/edit texts
    private var titleEditText: EditText? = null
    private var monthAutoText: AutoCompleteTextView? = null
    private var dayAutoText: AutoCompleteTextView? = null
    private var yearAutoText: AutoCompleteTextView? = null
    private var hourAutoText: AutoCompleteTextView? = null
    private var minuteAutoText: AutoCompleteTextView? = null
    private var whoAutoText: AutoCompleteTextView? = null

    // To calculate the days of a month
    private var days: MutableList<Int> = mutableListOf()

    // Save button
    private var saveButton: MaterialButton? = null

    // Adapter & position
    private var whoAdapter: ArrayAdapter<Contact>? = null
    private var position: Int? = null

    // Errors
    private var formHasErrors: Array<Boolean> = arrayOf(true, true, true, true, true, true, true)

    // Parsed date
    private var parsedDate: MutableList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactsDB = context?.applicationContext?.let { ContactsDB(it) }
        agendaDB = context?.applicationContext?.let { AgendaDB(it) }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_appointment_form, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        if (appointment != null) {
            formHasErrors = arrayOf(false, false, false, false, false, false, false)
            parseDate()
        }

        setUpTexts(view)
        setupButtons(view)

        return view
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val activity: Activity? = activity
        if (activity is DialogInterface.OnDismissListener)
            (activity as DialogInterface.OnDismissListener).onDismiss(dialog)
    }

    private fun setUpTexts(view: View) {
        titleInputLayout = view.findViewById(R.id.appointment_form_input_layout_title)
        monthInputLayout = view.findViewById(R.id.appointment_form_spinner_months)
        dayInputLayout = view.findViewById(R.id.appointment_form_spinner_days)
        yearInputLayout = view.findViewById(R.id.appointment_form_spinner_years)
        hourInputLayout = view.findViewById(R.id.appointment_form_spinner_hours)
        minuteInputLayout = view.findViewById(R.id.appointment_form_spinner_minutes)
        whoInputLayout = view.findViewById(R.id.appointment_form_spinner_who)

        titleEditText = view.findViewById(R.id.appointment_form_title)
        if (operation == Operation.UPDATE) titleEditText?.setText(appointment?.title ?: "")
        titleEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ignore
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Ignore
            }

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.isBlank()) {
                    titleInputLayout?.error = "Not a valid title"
                    formHasErrors[0] = true
                } else {
                    titleInputLayout?.error = ""
                    formHasErrors[0] = false
                }
            }
        })

        val dayAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                days
        )

        monthAutoText = view.findViewById(R.id.appointment_form_auto_months)
        if (appointment != null) {
            monthInputLayout?.placeholderText = parsedDate?.get(1)
            monthAutoText?.setText(parsedDate?.get(1))
        }
        val monthAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                Constants.months.keys.toMutableList()
        )
        monthAutoText?.setAdapter(monthAdapter)
        monthAutoText?.setOnItemClickListener { _, _, position, _ ->
            monthInputLayout?.placeholderText = monthAdapter.getItem(position) ?: ""
            formHasErrors[1] = false
            monthInputLayout?.error = ""
            if (checkSelected()) {
                dayInputLayout?.isEnabled = true
                val day = YearMonth.of(
                        yearInputLayout!!.placeholderText.toString().toInt(),
                        Constants.months[monthInputLayout?.placeholderText]
                ).lengthOfMonth()

                days = (1..day).toMutableList()
                dayAdapter.clear()
                dayAdapter.addAll(days)
            }
        }

        yearAutoText = view.findViewById(R.id.appointment_form_auto_years)
        if (appointment != null) {
            yearInputLayout?.placeholderText = parsedDate?.get(2)
            yearAutoText?.setText(parsedDate?.get(2))
        }
        val yearAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                mutableListOf("2021", "2022", "2023", "2024", "2025")
        )
        yearAutoText?.setAdapter(yearAdapter)
        yearAutoText?.setOnItemClickListener { _, _, position, _ ->
            yearInputLayout?.placeholderText = yearAdapter.getItem(position) ?: ""
            formHasErrors[2] = false
            yearInputLayout?.error = ""
            if (checkSelected()) {
                dayInputLayout?.isEnabled = true
                val day = YearMonth.of(
                        yearInputLayout!!.placeholderText.toString().toInt(),
                        Constants.months[monthInputLayout?.placeholderText]
                ).lengthOfMonth()

                days = (1..day).toMutableList()
                dayAdapter.clear()
                dayAdapter.addAll(days)
            }
        }

        dayAutoText = view.findViewById(R.id.appointment_form_auto_days)
        if (appointment != null) {
            dayInputLayout?.placeholderText = parsedDate?.get(0)
            dayAutoText?.setText(parsedDate?.get(0))
        }
        dayAutoText?.setAdapter(dayAdapter)
        dayAutoText?.setOnItemClickListener { _, _, position, _ ->
            dayInputLayout?.placeholderText = dayAdapter.getItem(position)?.toString() ?: ""
            formHasErrors[3] = false
            dayInputLayout?.error = ""
        }

        hourAutoText = view.findViewById(R.id.appointment_form_auto_hours)
        if (appointment != null){
            hourInputLayout?.placeholderText = parsedDate?.get(3)
            hourAutoText?.setText(parsedDate?.get(3))
        }
        val hourAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                (0..23).toMutableList()
        )
        hourAutoText?.setAdapter(hourAdapter)
        hourAutoText?.setOnItemClickListener { _, _, position, _ ->
            hourInputLayout?.placeholderText = hourAdapter.getItem(position)?.toString() ?: ""
            formHasErrors[4] = false
            hourInputLayout?.error = ""
        }

        minuteAutoText = view.findViewById(R.id.appointment_form_auto_minutes)
        if (appointment != null){
            minuteInputLayout?.placeholderText = parsedDate?.get(4)
            minuteAutoText?.setText(parsedDate?.get(4))
        }
        val minuteAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                (0..59).toMutableList()
        )
        minuteAutoText?.setAdapter(minuteAdapter)
        minuteAutoText?.setOnItemClickListener { _, _, position, _ ->
            minuteInputLayout?.placeholderText = minuteAdapter.getItem(position)?.toString() ?: ""
            formHasErrors[5] = false
            minuteInputLayout?.error = ""
        }

        whoAutoText = view.findViewById(R.id.appointment_form_auto_who)
        if (appointment != null) {
            val aux = contactsDB?.
            getContactById(
                    appointment?.with!!,
                    arrayOf("_id", "name", "phoneNumber", "about")
            )?.name
            whoInputLayout?.placeholderText = aux
            whoAutoText?.setText(aux)
        }
        whoAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                contactsDB?.getAllContactsSimple("name")?.toMutableList() ?: mutableListOf()
        )
        whoAutoText?.setAdapter(whoAdapter)
        whoAutoText?.setOnItemClickListener { _, _, position, _ ->
            whoInputLayout?.placeholderText = whoAdapter!!.getItem(position)?.toString() ?: ""
            this.position = position
            formHasErrors[6] = false
            whoInputLayout?.error = ""
        }
    }

    private fun checkSelected(): Boolean {
        return !(monthInputLayout?.placeholderText.isNullOrBlank()
                || yearInputLayout?.placeholderText.isNullOrBlank())
    }

    private fun setupButtons(view: View) {
        saveButton = view.findViewById(R.id.appointment_form_save)
        when (operation) {
            Operation.INSERT -> saveButton?.setText(R.string.save)
            Operation.UPDATE -> saveButton?.setText(R.string.update)
        }
        saveButton?.setOnClickListener(this)

        closeButton = view.findViewById(R.id.appointment_form_close_button)
        closeButton?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.appointment_form_close_button -> dismiss()
            R.id.appointment_form_save -> {
                val success: Boolean?
                if (!checkErrors()
                        && operation == Operation.INSERT) {
                    val toSave = Appointment(
                            id = null,
                            title = titleEditText?.text.toString(),
                            dateTime = "${dayInputLayout?.placeholderText}/" +
                                    "${monthInputLayout?.placeholderText}/" +
                                    "${yearInputLayout?.placeholderText} " +
                                    "${hourInputLayout?.placeholderText}:" +
                                    "${minuteInputLayout?.placeholderText}",
                            with = whoAdapter?.getItem(position!!)?.id
                    )
                    success = agendaDB?.insertAppointment(toSave)
                } else if (!checkErrors() && operation == Operation.UPDATE) {
                    val toUpdate = Appointment(
                            id = appointment?.id,
                            title = titleEditText?.text.toString(),
                            dateTime = "${dayInputLayout?.placeholderText}/" +
                                    "${monthInputLayout?.placeholderText}/" +
                                    "${yearInputLayout?.placeholderText} " +
                                    "${hourInputLayout?.placeholderText}:" +
                                    "${minuteInputLayout?.placeholderText}",
                            with = if (position != null) whoAdapter?.getItem(position!!)?.id
                                else appointment?.with
                    )

                    success = agendaDB?.updateAppointment(toUpdate)
                } else {
                    success = false
                }

                if (success!!) {
                    Toast.makeText(
                            requireContext(),
                            "Successfully saved",
                            Toast.LENGTH_SHORT).show()
                    dialog?.dismiss()
                } else {
                    Toast.makeText(
                            requireContext(),
                            "Oops! Could not save contact...",
                            Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkErrors(): Boolean {
        if(formHasErrors[0]) titleInputLayout?.error = "Not a valid title"
        else titleInputLayout?.error = ""
        if(formHasErrors[1]) monthInputLayout?.error = "You must select a month"
        else monthInputLayout?.error = ""
        if(formHasErrors[2]) yearInputLayout?.error = "You must select a year"
        else yearInputLayout?.error = ""
        if(formHasErrors[3]) dayInputLayout?.error = "You must select a day"
        else dayInputLayout?.error = ""
        if(formHasErrors[4]) hourInputLayout?.error = "You must select a hour"
        else hourInputLayout?.error = ""
        if(formHasErrors[5]) minuteInputLayout?.error = "You must select a minute"
        else minuteInputLayout?.error = ""
        if(formHasErrors[6]) whoInputLayout?.error = "You must select a contact"
        else whoInputLayout?.error = ""

        return formHasErrors[0] || formHasErrors[1] || formHasErrors[2] || formHasErrors[3]
                || formHasErrors[4] || formHasErrors[5] || formHasErrors[6] || isDateBeforeNow()!!
    }

    private fun isDateBeforeNow(): Boolean? {
        val date: String = "${dayInputLayout?.placeholderText}-" +
                "${monthInputLayout?.placeholderText}-" +
                "${yearInputLayout?.placeholderText} " +
                "${hourInputLayout?.placeholderText}:" +
                "${minuteInputLayout?.placeholderText}"

        var selectedDate: Date? = null

        try {
            selectedDate = SimpleDateFormat("dd-MMMM-yyyy HH:mm").parse(date)
        } catch (exception: Exception) {
            Toast.makeText(
                    context,
                    "Oops! Could not parse date...",
                    Toast.LENGTH_LONG
            ).show()
        }

        return selectedDate?.before(Date())
    }

    private fun parseDate() {
        val aux = appointment!!.dateTime?.split("/")?.toMutableList()
        val mAux = aux!![2].split(" ").toMutableList()
        val anotherAux = mAux[1].split(":")
        aux.removeLast()
        mAux.removeLast()
        parsedDate = mutableListOf()
        parsedDate?.addAll(aux)
        parsedDate?.addAll(mAux)
        parsedDate?.addAll(anotherAux)
    }
}