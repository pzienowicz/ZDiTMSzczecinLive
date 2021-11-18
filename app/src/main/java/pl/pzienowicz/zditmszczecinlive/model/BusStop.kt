package pl.pzienowicz.zditmszczecinlive.model

class BusStop {

    val id: Int? = null
    val nrzespolu: Int? = null
    val nrslupka: String? = null
    val nazwa: String? = null

    val numer: String
        get() = nrzespolu.toString() + nrslupka
}
