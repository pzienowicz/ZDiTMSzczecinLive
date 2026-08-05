package pl.pzienowicz.zditmszczecinlive.data

import org.junit.Assert.assertEquals
import org.junit.Test
import pl.pzienowicz.zditmszczecinlive.model.Board

class FavouriteDepartureFormatterTest {

    private val formatter = FavouriteDepartureFormatter(
        object : FavouriteDepartureFormatter.Strings {
            override fun departingNow(): String = "teraz"
            override fun departingInMinutes(minutes: Int): String = "za $minutes min"
            override fun departingAtTime(time: String): String = "o $time"
        }
    )

    @Test
    fun formatUsesRealTimeMinutes() {
        val departure = departure(line = "75", timeReal = 7, timeScheduled = "12:40")

        assertEquals("75 za 7 min", formatter.format(departure))
    }

    @Test
    fun formatUsesNowForZeroRealTimeMinutes() {
        val departure = departure(line = "8", timeReal = 0, timeScheduled = "12:40")

        assertEquals("8 teraz", formatter.format(departure))
    }

    @Test
    fun formatUsesScheduledTimeWhenRealTimeIsMissing() {
        val departure = departure(line = "87", timeReal = null, timeScheduled = "12:40")

        assertEquals("87 o 12:40", formatter.format(departure))
    }

    @Test
    fun formatSkipsMissingTime() {
        val departure = departure(line = "B", timeReal = null, timeScheduled = null)

        assertEquals("B", formatter.format(departure))
    }

    private fun departure(
        line: String,
        timeReal: Int?,
        timeScheduled: String?
    ): Board.Departure =
        Board.Departure(
            line_number = line,
            direction = "Kierunek",
            time_real = timeReal,
            time_scheduled = timeScheduled
        )
}
