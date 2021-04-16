package com.figueroa.contacts_prctica3.contacts.details

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.figueroa.contacts_prctica3.R


class ContactDetailsDialogFragment(
        private val contactId: Int,
        private val contactName: String,
        private val contactPhone: String,
        private val contactAbout: String
) : DialogFragment(),
    View.OnClickListener {

    private var nameTextView: TextView? = null
    private var phoneTextView: TextView? = null
    private var aboutTextView: TextView? = null

    private var closeButton: ImageButton? = null
    private var callButton: Button? = null
    private var messageButton: Button? = null
    private var appointmentButton: Button? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_contact_details, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        setupTextAndButtons(view)

        return view
    }

    private fun setupTextAndButtons(view: View) {
        nameTextView = view.findViewById(R.id.contact_details_name)
        phoneTextView = view.findViewById(R.id.contact_details_phone)
        aboutTextView = view.findViewById(R.id.contact_details_about)

        closeButton = view.findViewById(R.id.contact_details_close_button)
        closeButton?.setOnClickListener(this)
        callButton = view.findViewById(R.id.contact_details_call_button)
        callButton?.setOnClickListener(this)
        messageButton = view.findViewById(R.id.contact_details_message_button)
        messageButton?.setOnClickListener(this)

        nameTextView?.text = contactName
        phoneTextView?.text = contactPhone
        aboutTextView?.text = contactAbout

        if (contactId < 0) {
            callButton?.isEnabled = false
            messageButton?.isEnabled = false
            appointmentButton?.isEnabled = false
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.contact_details_close_button -> {
                dismiss()
            }
            R.id.contact_details_call_button -> {
                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel: $contactPhone"))
                startActivity(dialIntent)
            }
            R.id.contact_details_message_button -> {
                val smsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("sms: $contactPhone"))
                startActivity(smsIntent)
            }
        }
    }

}