package com.figueroa.contacts_prctica3.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.figueroa.contacts_prctica3.models.Appointment
import com.figueroa.contacts_prctica3.models.Contact

class ContactsDB(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CONTACT_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS contacts")
        onCreate(db)
    }

    /**
     * getContactById
     *
     * description:
     * Retrieves a contact by its id
     * params:
     * - int id
     * - String[] fieldsToRetrieve
     * returns:
     * The contact matching the specified id; null otherwise
     */
    fun getContactById(id: Int, fieldsToRetrieve: Array<String>?): Contact? {
        var res: Contact?
        val cursor: Cursor
        try {
            val db = readableDatabase
            cursor = db.query("contacts", fieldsToRetrieve, "_id = $id",
                null, null, null, null, null)
            cursor.moveToFirst()
            res = Contact(cursor.getInt(0), cursor.getString(1),
                cursor.getString(2), cursor.getString(3))
            db.close()
            cursor.close()
        } catch (dbError: SQLiteException) {
            Log.e("DB_ERROR", dbError.message!!)
            res = null
        } catch (exception: Exception) {
            Log.e("RETRIEVE_ERROR", exception.message!!)
            res = null
        }
        return res
    }

    /**
     * getAllContacts
     *
     * description:
     * Retrieves all contacts stored in the db
     * params:
     * - String[] fieldsToRetrieve
     * - String orderBy
     * returns:
     * Every contact in the database or an empty list if the database is empty; null otherwise
     */
    fun getAllContactsSimple(orderBy: String): MutableList<Contact>? {
        var res: MutableList<Contact>?
        val cursor: Cursor
        val fieldsToRetrieve = arrayOf("_id", "name", "phoneNumber", "about")
        try {
            val db = readableDatabase
            cursor = db.query("contacts", fieldsToRetrieve, null,
                null, null, null, orderBy, null)
            cursor.moveToFirst()
            res = ArrayList()
            if (cursor.count >= 1) {
                while (!cursor.isAfterLast) {
                    res.add(Contact(cursor.getInt(0), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3)))
                    cursor.moveToNext()
                }
            }
            db.close()
            cursor.close()
        } catch (dbError: SQLiteException) {
            Log.e("DB_ERROR", dbError.message!!)
            res = null
        } catch (exception: Exception) {
            Log.e("RETRIEVE_ERROR", exception.message!!)
            res = null
        }
        return res
    }

    /**
     * insertContact
     *
     * description:
     * Writes a contact to the database
     * params:
     * - Contact contact
     * returns:
     * A boolean that is true when the transaction is completed successfully
     */
    fun insertContact(contact: Contact): Boolean {
        var res: Boolean
        val values: ContentValues
        try {
            val db = writableDatabase
            values = ContentValues()
            values.put("name", contact.name)
            values.put("phoneNumber", contact.phoneNumber)
            values.put("about", contact.about)
            db.insert("contacts", null, values)
            db.close()
            res = true
        } catch (dbError: SQLiteException) {
            Log.e("DB_ERROR", dbError.message!!)
            res = false
        } catch (e: Exception) {
            Log.e("INSERT_ERROR", e.message!!)
            res = false
        }
        return res
    }

    /**
     * insertContact
     *
     * description:
     * Updates a contact from the database
     * params:
     * - Contact contact
     * returns:
     * A boolean that is true when the transaction is completed successfully
     */
    fun updateContact(contact: Contact): Boolean {
        var res: Boolean
        val values: ContentValues
        try {
            val db = writableDatabase
            values = ContentValues()
            values.put("name", contact.name)
            values.put("phoneNumber", contact.phoneNumber)
            values.put("about", contact.about)
            db.update("contacts", values, "_id = " + contact.id,
                null)
            db.close()
            res = true
        } catch (dbError: SQLiteException) {
            Log.e("DB_ERROR", dbError.message!!)
            res = false
        } catch (e: Exception) {
            Log.e("INSERT_ERROR", e.message!!)
            res = false
        }
        return res
    }

    /**
     * deleteContact
     *
     * description:
     * Delete a contact from the database
     * params:
     * - Contact contact
     * returns:
     * A boolean that is true when the transaction is completed successfully
     */
    fun deleteContact(contact: Contact?): Boolean {
        val res: Boolean
        res = try {
            val db = writableDatabase
            db.delete("contacts", "_id = " + contact?.id,
                null)
            db.close()
            true
        } catch (dbError: SQLiteException) {
            Log.e("DB_ERROR", dbError.message!!)
            false
        } catch (e: Exception) {
            Log.e("INSERT_ERROR", e.message!!)
            false
        }
        return res
    }

    fun findByName(contactName: String): MutableList<Contact>? {
        var res: MutableList<Contact>? = null
        lateinit var cursor: Cursor

        try {
            val db = readableDatabase
            cursor = db.rawQuery("SELECT _id, name, phoneNumber FROM contacts " +
                    "WHERE name LIKE '%$contactName%' ORDER BY name",  null)
            cursor.moveToFirst()

            res = mutableListOf()
            if (cursor.count >= 1) {
                while (!cursor.isAfterLast) {
                    res.add(Contact(cursor.getInt(0), cursor.getString(1),
                        null, cursor.getString(2)))
                    cursor.moveToNext()
                }
            }
            db.close()
            cursor.close()
        } catch (e: Exception) {
            Log.e("RETRIEVE_ERROR", e.message!!)
        }

        return res
    }

    companion object {
        private const val DB_VERSION = 1
        private const val DB_NAME = "Contacts.db"
        private const val CONTACT_TABLE = "CREATE TABLE contacts " +
                "(_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                " name VARCHAR(255), phoneNumber VARCHAR(255)," +
                " about VARCHAR(255) )"
    }
}

class AgendaDB(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(AGENDA_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS contacts")
        onCreate(db)
    }

    /**
     * getAppointmentById
     *
     * description:
     * Retrieves an appointment by its id
     * params:
     * - int id
     * - String[] fieldsToRetrieve
     * returns:
     * The contact matching the specified id; null otherwise
     */
    fun getAppointmentById(id: Int, fieldsToRetrieve: Array<String>?): Appointment? {
        var res: Appointment?
        val cursor: Cursor
        try {
            val db = readableDatabase
            cursor = db.query("agenda", fieldsToRetrieve, "_id = $id",
                    null, null, null, null, null)
            cursor.moveToFirst()
            res = Appointment(cursor.getInt(0), cursor.getString(1),
                    cursor.getString(2), cursor.getInt(3))
            db.close()
            cursor.close()
        } catch (dbError: SQLiteException) {
            Log.e("DB_ERROR", dbError.message!!)
            res = null
        } catch (exception: Exception) {
            Log.e("RETRIEVE_ERROR", exception.message!!)
            res = null
        }
        return res
    }

    fun findByDate(appointmentDate: String): MutableList<Appointment>? {
        var res: MutableList<Appointment>? = null
        lateinit var cursor: Cursor

        try {
            val db = readableDatabase
            cursor = db.rawQuery("SELECT _id, title, dateTime, with FROM agenda " +
                    "WHERE dateTime LIKE '%$appointmentDate%' ORDER BY dateTime",  null)
            cursor.moveToFirst()

            res = mutableListOf()
            if (cursor.count >= 1) {
                while (!cursor.isAfterLast) {
                    res.add(Appointment(cursor.getInt(0), cursor.getString(1),
                            cursor.getString(2), cursor.getInt(3)))
                    cursor.moveToNext()
                }
            }
            db.close()
            cursor.close()
        } catch (e: Exception) {
            Log.e("RETRIEVE_ERROR", e.message!!)
        }

        return res
    }

    /**
     * getAllAppointments
     *
     * description:
     * Retrieves all appointments stored in the db
     * params:
     * - String[] fieldsToRetrieve
     * - String orderBy
     * returns:
     * Every appointments in the database or an empty list if the database is empty; null otherwise
     */
    fun getAllAppointmentsSimple(orderBy: String): MutableList<Appointment>? {
        var res: MutableList<Appointment>?
        val cursor: Cursor
        val fieldsToRetrieve = arrayOf("_id", "title", "dateTime", "with")
        try {
            val db = readableDatabase
            cursor = db.query("agenda", fieldsToRetrieve, null,
                    null, null, null, null, null)
            cursor.moveToFirst()
            res = ArrayList()
            if (cursor.count >= 1) {
                while (!cursor.isAfterLast) {
                    res.add(Appointment(cursor.getInt(0), cursor.getString(1),
                            cursor.getString(2), cursor.getInt(3)))
                    cursor.moveToNext()
                }
            }
            db.close()
            cursor.close()
        } catch (dbError: SQLiteException) {
            Log.e("DB_ERROR", dbError.message!!)
            res = null
        } catch (exception: Exception) {
            Log.e("RETRIEVE_ERROR", exception.message!!)
            res = null
        }
        return res
    }

    /**
     * insertAppointment
     *
     * description:
     * Writes a appointment to the database
     * params:
     * - Appointment appointment
     * returns:
     * A boolean that is true when the transaction is completed successfully
     */
    fun insertAppointment(appointment: Appointment): Boolean {
        var res: Boolean
        val values: ContentValues
        try {
            val db = writableDatabase
            values = ContentValues()
            values.put("title", appointment.title)
            values.put("dateTime", appointment.dateTime)
            values.put("with", appointment.with)
            db.insert("agenda", null, values)
            db.close()
            res = true
        } catch (dbError: SQLiteException) {
            Log.e("DB_ERROR", dbError.message!!)
            res = false
        } catch (e: Exception) {
            Log.e("INSERT_ERROR", e.message!!)
            res = false
        }
        return res
    }

    /**
     * insertAppointment
     *
     * description:
     * Updates a appointment from the database
     * params:
     * - Appointment appointment
     * returns:
     * A boolean that is true when the transaction is completed successfully
     */
    fun updateAppointment(appointment: Appointment): Boolean {
        var res: Boolean
        val values: ContentValues
        try {
            val db = writableDatabase
            values = ContentValues()
            values.put("title", appointment.title)
            values.put("dateTime", appointment.dateTime)
            values.put("with", appointment.with)
            db.update("agenda", values, "_id = " + appointment.id,
                    null)
            db.close()
            res = true
        } catch (dbError: SQLiteException) {
            Log.e("DB_ERROR", dbError.message!!)
            res = false
        } catch (e: Exception) {
            Log.e("INSERT_ERROR", e.message!!)
            res = false
        }
        return res
    }

    /**
     * deleteAppointment
     *
     * description:
     * Delete a appointment from the database
     * params:
     * - Appointment appointment
     * returns:
     * A boolean that is true when the transaction is completed successfully
     */
    fun deleteAppointment(appointment: Appointment?): Boolean {
        val res: Boolean
        res = try {
            val db = writableDatabase
            db.delete("agenda", "_id = " + appointment?.id,
                    null)
            db.close()
            true
        } catch (dbError: SQLiteException) {
            Log.e("DB_ERROR", dbError.message!!)
            false
        } catch (e: Exception) {
            Log.e("INSERT_ERROR", e.message!!)
            false
        }
        return res
    }

    companion object {
        private const val DB_VERSION = 1
        private const val DB_NAME = "Agenda.db"
        private const val AGENDA_TABLE = "CREATE TABLE agenda " +
                "(_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                " title VARCHAR(255), dateTime VARCHAR(255)," +
                " with INTEGER )"
    }
}