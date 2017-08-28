package pl.pzienowicz.zditmszczecinlive.model

import com.google.gson.annotations.SerializedName

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar

class Info {

    private val dateFormat: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy")

    @SerializedName("od")
    val from: String? = null

    @SerializedName("do")
    val to: String? = null

    @SerializedName("opis")
    val description: String? = null

    val toDate: String?
        get() {
            if (to == null) {
                return null
            }

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = java.lang.Long.parseLong(to) * 1000

            return dateFormat.format(calendar.time)
        }

    val fromDate: String?
        get() {
            if (from == null) {
                return null
            }

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = java.lang.Long.parseLong(from) * 1000

            return dateFormat.format(calendar.time)
        }
}
