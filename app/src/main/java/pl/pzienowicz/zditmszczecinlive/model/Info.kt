package pl.pzienowicz.zditmszczecinlive.model

import java.text.SimpleDateFormat

data class Info(
    val valid_from: String,
    val valid_to: String? = null,
    val description: Description
) {
    val toDate: String?
        get() {
            if (valid_to == null) {
                return null
            }

            val dateFormatFrom = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            val dateFormatTo = SimpleDateFormat("yyyy-MM-dd")

            val date = dateFormatFrom.parse(valid_to)
            return date?.let {
                dateFormatTo.format(it)
            }
        }

    val fromDate: String
        get() {
            val dateFormatFrom = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            val dateFormatTo = SimpleDateFormat("yyyy-MM-dd")

            val date = dateFormatFrom.parse(valid_from)
            return date?.let {
                dateFormatTo.format(it)
            } ?: ""
        }

    data class Description(val pl: String)
}
