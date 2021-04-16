package com.figueroa.contacts_prctica3.contacts.form

import android.app.Activity
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.figueroa.contacts_prctica3.R
import com.figueroa.contacts_prctica3.db.ContactsDB
import com.figueroa.contacts_prctica3.db.Operation
import com.figueroa.contacts_prctica3.models.Contact
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class ContactFormFragment(
        private val operation: Operation,
        private var contact: Contact?
        ) :
        DialogFragment(), View.OnClickListener {

    // DB
    private var db: ContactsDB? = null

    // Close button
    private var closeButton: ImageButton? = null

    // Text input layouts
    private var nameInputLayout: TextInputLayout? = null
    private var phoneInputLayout: TextInputLayout? = null
    private var aboutInputLayout: TextInputLayout? = null

    // Edit texts
    private var nameEditText: EditText? = null
    private var phoneEditText: EditText? = null
    private var aboutEditText: EditText? = null

    // Save button
    private var saveButton: MaterialButton? = null

    // Errors
    private var formHasErrors: Array<Boolean> = arrayOf(true, true, true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = context?.applicationContext?.let { ContactsDB(it) }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_contact_form, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        if (contact != null) formHasErrors = arrayOf(false, false, false)

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
        nameInputLayout = view.findViewById(R.id.contact_form_input_layout_name)
        phoneInputLayout = view.findViewById(R.id.contact_form_input_layout_phone)
        aboutInputLayout = view.findViewById(R.id.contact_form_input_layout_about)

        nameEditText = view.findViewById(R.id.contact_form_name)
        if (operation == Operation.UPDATE) nameEditText?.setText(contact?.name ?: "")
        nameEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ignore
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Ignore
            }

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.isBlank()) {
                    nameInputLayout?.error = "Not a valid name"
                    formHasErrors[0] = true
                } else {
                    nameInputLayout?.error = null
                    formHasErrors[0] = false
                }
            }

        })

        phoneEditText = view.findViewById(R.id.contact_form_phone)
        if (operation == Operation.UPDATE) phoneEditText?.setText(contact?.phoneNumber ?: "")
        phoneEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ignore
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Ignore
            }

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.isBlank()) {
                    phoneInputLayout?.error = "Not a valid phone"
                    formHasErrors[1] = true
                } else {
                    phoneInputLayout?.error = null
                    formHasErrors[1] = false
                }
            }

        })

        aboutEditText = view.findViewById(R.id.contact_form_about)
        if (operation == Operation.UPDATE) aboutEditText?.setText(contact?.about ?: "")
        aboutEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ignore
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Ignore
            }

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.isBlank()) {
                    aboutInputLayout?.error = "Not a valid about"
                    formHasErrors[2] = true
                } else {
                    aboutInputLayout?.error = null
                    formHasErrors[2] = false
                }
            }

        })
    }

    private fun setupButtons(view: View) {
        saveButton = view.findViewById(R.id.contact_form_save)
        when (operation) {
            Operation.INSERT -> saveButton?.setText(R.string.save)
            Operation.UPDATE -> saveButton?.setText(R.string.update)
        }
        saveButton?.setOnClickListener(this)

        closeButton = view.findViewById(R.id.contact_form_close_button)
        closeButton?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.contact_form_close_button -> dismiss()
            R.id.contact_form_save -> {
                val success: Boolean?
                if (checkErrors()
                        && operation == Operation.INSERT) {
                    val toSave = Contact(null,
                            name = nameEditText?.text.toString(),
                            phoneNumber = phoneEditText?.text.toString(),
                            about = aboutEditText?.text.toString()
                    )
                    success = db?.insertContact(toSave)
                } else if (checkErrors() && operation == Operation.UPDATE) {
                    val name = nameEditText?.text.toString()
                    val phone = phoneEditText?.text.toString()
                    val about = aboutEditText?.text.toString()

                    val toUpdate = Contact(
                            id = contact?.id ?: -1,
                            name = if (name.isNotBlank()) name
                            else contact?.name.toString(),
                            phoneNumber = if (phone.isNotBlank()) phone
                            else contact?.phoneNumber.toString(),
                            about = if (about.isNotBlank()) about
                            else contact?.about.toString(),
                    )

                    success = db?.updateContact(toUpdate)
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

    private fun checkErrors() = (!formHasErrors[0] && !formHasErrors[1]
            && !formHasErrors[2])

}