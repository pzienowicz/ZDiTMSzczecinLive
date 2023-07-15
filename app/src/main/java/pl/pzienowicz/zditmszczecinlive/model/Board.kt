package pl.pzienowicz.zditmszczecinlive.model

class Board {

    val stop_name: String? = null
    val stop_number: String? = null
    val departures: List<Departure>? = null
    val message: String? = null

    data class Departure(
        val line_number: String,
        val direction: String,
        val time_real: Int?,
        val time_scheduled: String?
    )
}
