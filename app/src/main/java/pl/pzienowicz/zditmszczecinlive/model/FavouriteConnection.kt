package pl.pzienowicz.zditmszczecinlive.model

data class FavouriteConnection(
    val stopId: Int,
    val stopNumber: String,
    val stopName: String,
    val lineNumber: String,
    val direction: String
)
