package pl.pzienowicz.zditmszczecinlive

object Config {

    val LOG_TAG = "Komunikacja"

    const val ACRA_URL = "https://2e848b43-ebfa-400e-855a-8e4330eb1168-bluemix.cloudant.com/acra-zditmszczecin/_design/acra-storage/_update/report"
    const val ACRA_USER = "reemeningedsiblerstlesin"
    const val ACRA_PASS = "2f2ae885f268c66285eeb5ecebc591573065103d"

    val LICENSEE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwUacKgf7yYA3XFsPCYAM70Bje6pYsfm5nJ6NKbFTDL+mVD5kF1mVWoGT0XJoitIkZFpIQmcz3cudDMpwIQ4Cfs6r+v2am0TruHDo8hbtvdlIrrrqQM23V4j5xtyd2X4zt1NXVNLx9ZYFL+kDa5q/MQ4wuLqP/12+m9OhmKl98p9CRwgpaUXW973M3fhmXoyQ5sD4q+XAWBl1rXpBnKrcd9nD9mhSGPa/Qndc8onMypOVgNdPwQa0uMpy9XnPVLXgSOmHP8WZK6W+pvtH5ojbln10C5zWUNIyOYmUC2Wj5t/wl0bL4ATiWayXa3IYC+VdyLvLHtOprPTcyj7Rhg11qQIDAQAB"
    val PRODUCT_WIDGETS_UNLOCK = "widgets_unlock"

    val URL = "http://www.zditm.szczecin.pl/mapy/przystanki-i-pojazdy"
    val LINE_URL = "http://www.zditm.szczecin.pl/mapy/linia,"
    val BASE_URL = "http://www.zditm.szczecin.pl/"
    val BUS_STOP_URL = "http://www.zditm.szczecin.pl/pasazer/rozklady-jazdy,tablica,"
    val PREFERENCE_SHOW_DIALOG = "showDialog"
    val PREFERENCE_FAVOURITE_MAP = "favouriteMap"

    val LINES_PER_ROW = 5
    val ZONES_PER_ROW_LANDSCAPE = 9

    val PREFERENCE_SELECTED_LINE = "selectedLine"
    val INTENT_LOAD_NEW_URL = "loadNewUrl"
    val INTENT_REFRESH_SETTINGS = "refreshSettings"
    val INTENT_REFRESH_WIDGETS_LIST = "refreshWidgetsList"
    val INTENT_OPEN_BUSSTOP_EDIT = "editBusStop"
    val INTENT_NO_INTERNET_CONNETION = "noInternet"
    val INTENT_WIDGET_DATA_LOADED = "widgetDataLoaded"
    val EXTRA_LINE_ID = "lineId"
    val EXTRA_BUS_STOP_NUMBER = "busStopNumber"
    val EXTRA_WIDGET_ID = "widgetId"

    val PREFERENCE_USE_LOCATION = "useLocation"
    val PREFERENCE_ZOOM_MAP = "zoomMap"

    val CLICK_WIDGET_BUTTON = "clickWidgetButton"
    val ACTION_AUTO_UPDATE = "AUTO_UPDATE"

    val WIDGET_PREFIX = "widget_"
}
