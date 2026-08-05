package pl.pzienowicz.zditmszczecinlive.data

import android.content.Context
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.model.Board

class FavouriteDepartureFormatter(
    private val strings: Strings
) {

    fun format(departure: Board.Departure): String {
        val time = departure.time_real?.let {
            if (it == 0) {
                strings.departingNow()
            } else {
                strings.departingInMinutes(it)
            }
        } ?: departure.time_scheduled?.let {
            strings.departingAtTime(it)
        }.orEmpty()

        return listOf(departure.line_number, time)
            .filter { it.isNotBlank() }
            .joinToString(" ")
    }

    interface Strings {
        fun departingNow(): String
        fun departingInMinutes(minutes: Int): String
        fun departingAtTime(time: String): String
    }

    class AndroidStrings(private val context: Context) : Strings {
        override fun departingNow(): String =
            context.getString(R.string.departing_now)

        override fun departingInMinutes(minutes: Int): String =
            context.getString(R.string.departing_in_minutes, minutes)

        override fun departingAtTime(time: String): String =
            context.getString(R.string.departing_at_time, time)
    }
}
