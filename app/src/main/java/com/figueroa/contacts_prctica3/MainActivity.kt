package com.figueroa.contacts_prctica3

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.figueroa.contacts_prctica3.agenda.AgendaFragment
import com.figueroa.contacts_prctica3.agenda.form.AppointmentFormFragment
import com.figueroa.contacts_prctica3.contacts.form.ContactFormFragment

import com.figueroa.contacts_prctica3.contacts.list.ContactsFragment
import com.figueroa.contacts_prctica3.db.Operation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity :
    AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener,
    View.OnClickListener,
    DialogInterface.OnDismissListener {

    private var fab: FloatingActionButton? = null
    private var navPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNav)
        showFragment(ContactsFragment(), From.NONE)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        fab = findViewById(R.id.main_activity_fab)
        fab?.setOnClickListener(this)
    }

    private fun showFragment(fragment: Fragment, from: From) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        setAnim(from, transaction)
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()
    }

    private fun setAnim(from: From, transaction: FragmentTransaction) {
        when (from) {
            From.LEFT -> transaction.setCustomAnimations(
                    R.anim.enter_from_left,
                    R.anim.exit_to_right,
                    0, 0
            )
            From.RIGHT -> transaction.setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    0, 0
            )
            From.NONE -> transaction.setCustomAnimations(
                    0, 0, 0, 0
            )
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_agenda -> {
                if (navPosition == 0) {
                    showFragment(AgendaFragment(), From.RIGHT)
                    navPosition = 1
                    return true
                }
            }
            R.id.nav_contacts -> {
                if (navPosition == 1) {
                    showFragment(ContactsFragment(), From.LEFT)
                    navPosition = 0
                    return true
                }
            }
        }
        return false
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.main_activity_fab) {
            when (navPosition) {
                0 -> ContactFormFragment(
                        operation = Operation.INSERT,
                        contact = null
                ).show(
                        supportFragmentManager,
                        "contact_form"
                )
                1 -> AppointmentFormFragment(
                        operation = Operation.INSERT,
                        appointment = null
                ).show(
                        supportFragmentManager,
                        "appointment_form"
                )
            }

        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        when (navPosition) {
            0 -> showFragment(ContactsFragment(), From.NONE)
            1 -> showFragment(AgendaFragment(), From.NONE)
        }
    }
}
enum class From {
    LEFT, RIGHT, NONE
}