package com.figueroa.contacts_prctica3.models

class Contact
    (
    var id: Int?,
    var name: String?,
    var phoneNumber: String?,
    var about: String?
) {

    // toString
    override fun toString(): String {
        return name!!
    }
}

class Appointment
    (var id: Int?,
     var title: String?,
     var dateTime: String?,
     var with: Int?) {

    override fun toString(): String {
        return "Appointment{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", date='" + dateTime + '\'' +
                ", with='" + with + '\'' +
                '}'
    }
    }