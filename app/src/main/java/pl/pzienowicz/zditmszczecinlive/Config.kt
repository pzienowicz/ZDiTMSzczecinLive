package pl.pzienowicz.zditmszczecinlive

object Config {

    const val LOG_TAG = "Komunikacja"

    const val ACRA_URL = "https://2e848b43-ebfa-400e-855a-8e4330eb1168-bluemix.cloudant.com/acra-zditmszczecin/_design/acra-storage/_update/report"
    const val ACRA_USER = "reemeningedsiblerstlesin"
    const val ACRA_PASS = "2f2ae885f268c66285eeb5ecebc591573065103d"

    const val LICENSEE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwUacKgf7yYA3XFsPCYAM70Bje6pYsfm5nJ6NKbFTDL+mVD5kF1mVWoGT0XJoitIkZFpIQmcz3cudDMpwIQ4Cfs6r+v2am0TruHDo8hbtvdlIrrrqQM23V4j5xtyd2X4zt1NXVNLx9ZYFL+kDa5q/MQ4wuLqP/12+m9OhmKl98p9CRwgpaUXW973M3fhmXoyQ5sD4q+XAWBl1rXpBnKrcd9nD9mhSGPa/Qndc8onMypOVgNdPwQa0uMpy9XnPVLXgSOmHP8WZK6W+pvtH5ojbln10C5zWUNIyOYmUC2Wj5t/wl0bL4ATiWayXa3IYC+VdyLvLHtOprPTcyj7Rhg11qQIDAQAB"
    const val PRODUCT_WIDGETS_UNLOCK = "widgets_unlock"

    const val URL = "http://www.zditm.szczecin.pl/mapy/przystanki-i-pojazdy"
    const val LINE_URL = "http://www.zditm.szczecin.pl/mapy/linia,"
    const val BASE_URL = "http://www.zditm.szczecin.pl/"
    const val BUS_STOP_URL = "http://www.zditm.szczecin.pl/pasazer/rozklady-jazdy,tablica,"
    const val PREFERENCE_SHOW_DIALOG = "showDialog"
    const val PREFERENCE_FAVOURITE_MAP = "favouriteMap"

    const val LINES_PER_ROW = 5
    const val ZONES_PER_ROW_LANDSCAPE = 9

    const val PREFERENCE_SELECTED_LINE = "selectedLine"
    const val INTENT_LOAD_NEW_URL = "loadNewUrl"
    const val INTENT_REFRESH_SETTINGS = "refreshSettings"
    const val INTENT_REFRESH_WIDGETS_LIST = "refreshWidgetsList"
    const val INTENT_OPEN_BUSSTOP_EDIT = "editBusStop"
    const val INTENT_NO_INTERNET_CONNETION = "noInternet"
    const val INTENT_WIDGET_DATA_LOADED = "widgetDataLoaded"
    const val EXTRA_LINE_ID = "lineId"
    const val EXTRA_BUS_STOP_NUMBER = "busStopNumber"
    const val EXTRA_WIDGET_ID = "widgetId"

    const val PREFERENCE_USE_LOCATION = "useLocation"
    const val PREFERENCE_ZOOM_MAP = "zoomMap"
    const val PREFERENCE_WIDGETS_REFRESH = "widgetsRefresh"
    const val PREFERENCE_WIDGETS_REFRESH_TIME = "widgetsRefreshTime"

    const val CLICK_WIDGET_BUTTON = "clickWidgetButton"
    const val ACTION_AUTO_UPDATE = "AUTO_UPDATE"

    const val WIDGET_PREFIX = "widget_"
}