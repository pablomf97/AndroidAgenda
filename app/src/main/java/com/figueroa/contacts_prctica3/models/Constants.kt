package com.figueroa.contacts_prctica3.models

import java.time.Month

class Constants {
    companion object {
        val months: Map<String, Month> = mapOf(
                "January" to Month.JANUARY,
                "February" to Month.FEBRUARY,
                "March" to Month.MARCH,
                "April" to Month.APRIL,
                "May" to Month.MAY,
                "June" to Month.JUNE,
                "July" to Month.JULY,
                "August" to Month.AUGUST,
                "September" to Month.SEPTEMBER,
                "October" to Month.OCTOBER,
                "November" to Month.NOVEMBER,
                "December" to Month.DECEMBER
        )

        val monthsNumber: Map<Int, String> = mapOf(
                0 to "January",
                1 to "February",
                2 to "March",
                3 to "April",
                4 to "May",
                5 to "June",
                6 to "July",
                7 to "August",
                8 to "September",
                9 to "October",
                10 to "November",
                11 to "December"
        )
    }
}