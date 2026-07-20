package pl.pzienowicz.zditmszczecinlive.model

import com.google.gson.JsonElement
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.max

class Board {

    val data: BoardData? = null

    val departures: List<Departure>?
        get() = data?.departures?.map { it.toDeparture() }

    val message: String?
        get() = data?.messages
            ?.mapNotNull { it.toMessageText() }
            ?.filter { it.isNotBlank() }
            ?.joinToString(", ")
            ?.takeIf { it.isNotBlank() }

    data class Departure(
        val line_number: String,
        val direction: String,
        val time_real: Int?,
        val time_scheduled: String?
    )

    data class BoardData(
        val departures: List<ApiDeparture>?,
        val messages: List<JsonElement>?
    )

    data class ApiDeparture(
        val line: Line,
        val trip: Trip,
        val departure_time: DepartureTime
    ) {
        fun toDeparture(): Departure {
            val scheduledDate = parseDate(departure_time.scheduled)
            val estimatedDate = parseDate(departure_time.estimated)
            val timeReal = when {
                departure_time.departing_now -> 0
                departure_time.real_time -> estimatedDate?.minutesFromNow()
                else -> null
            }

            return Departure(
                line_number = line.number,
                direction = trip.headsign.short,
                time_real = timeReal,
                time_scheduled = scheduledDate?.toHourMinute()
            )
        }
    }

    data class Line(
        val number: String
    )

    data class Trip(
        val headsign: Headsign
    )

    data class Headsign(
        val short: String
    )

    data class DepartureTime(
        val scheduled: String,
        val estimated: String,
        val departing_now: Boolean,
        val real_time: Boolean
    )

    companion object {
        private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        private val hourMinuteFormat = SimpleDateFormat("HH:mm", Locale.US)

        private fun parseDate(value: String): Date? {
            val normalizedValue = value.replace(Regex("\\.(\\d{3})\\d*Z$"), ".$1Z")
            return runCatching {
                synchronized(apiDateFormat) {
                    apiDateFormat.parse(normalizedValue)
                }
            }.getOrNull()
        }

        private fun Date.minutesFromNow(): Int {
            val diffMillis = time - Date().time
            return max(0, ((diffMillis + MILLIS_IN_MINUTE - 1) / MILLIS_IN_MINUTE).toInt())
        }

        private fun Date.toHourMinute(): String = synchronized(hourMinuteFormat) {
            hourMinuteFormat.format(this)
        }

        private fun JsonElement.toMessageText(): String? {
            if (isJsonPrimitive) {
                return asString
            }

            if (!isJsonObject) {
                return null
            }

            val message = asJsonObject
            return message.get("pl")?.asString
                ?: message.get("message")?.asString
                ?: message.get("text")?.asString
        }

        private const val MILLIS_IN_MINUTE = 60_000
    }
}
