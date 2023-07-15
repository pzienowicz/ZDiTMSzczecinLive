package pl.pzienowicz.zditmszczecinlive.model

class Line(
    val id: Int,
    val number: String,
    val type: String,
    val subtype: String,
    val vehicle_type: String,
    val highlighted: Boolean,
    val on_demand: Boolean
)
