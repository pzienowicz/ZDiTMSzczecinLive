package pl.pzienowicz.zditmszczecinlive

object Config {

    val LOG_TAG = "Komunikacja"

    val URL = "http://www.zditm.szczecin.pl/mapy/przystanki-i-pojazdy"
    val LINE_URL = "http://www.zditm.szczecin.pl/mapy/linia,"
    val BASE_URL = "http://www.zditm.szczecin.pl/"
    val PREFERENCE_SHOW_DIALOG = "showDialog"
    val PREFERENCE_FAVOURITE_MAP = "favouriteMap"

    val LINES_PER_ROW = 5
    val ZONES_PER_ROW_LANDSCAPE = 9

    val PREFERENCE_SELECTED_LINE = "selectedLine"
    val INTENT_LOAD_NEW_URL = "loadNewUrl"
    val INTENT_REFRESH_SETTINGS = "refreshSettings"
    val INTENT_NO_INTERNET_CONNETION = "noInternet"
    val EXTRA_LINE_ID = "lineId"

    val PREFERENCE_USE_LOCATION = "useLocation"
    val PREFERENCE_ZOOM_MAP = "zoomMap"

}
