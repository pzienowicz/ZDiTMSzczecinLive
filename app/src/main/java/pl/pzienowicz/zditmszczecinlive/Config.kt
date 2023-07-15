package pl.pzienowicz.zditmszczecinlive

object Config {

    const val LOG_TAG = "Komunikacja"

    const val PRODUCT_WIDGETS_UNLOCK = "widgets_unlock"

    const val URL: String = "https://www.zditm.szczecin.pl/pl/mapy/przystanki-i-pojazdy"
    const val LINE_URL = "https://www.zditm.szczecin.pl/pl/mapy/linia/"
    const val BASE_URL = "https://www.zditm.szczecin.pl/"
    const val BUS_STOP_URL = "https://www.zditm.szczecin.pl/pl/pasazer/rozklady-jazdy/tablica/"
    const val PREFERENCE_SHOW_DIALOG = "showDialog"
    const val PREFERENCE_FAVOURITE_MAP = "favouriteMap"

    const val FB_GROUP_URL = "https://www.facebook.com/groups/1241981472572028"

    const val LINES_PER_ROW = 5
    const val LINES_PER_ROW_LANDSCAPE = 9

    const val CAMERAS_PER_ROW = 2
    const val CAMERAS_PER_ROW_LANDSCAPE = 3

    const val PREFERENCE_SELECTED_LINE = "selectedLine"
    const val INTENT_LOAD_NEW_URL = "loadNewUrl"
    const val INTENT_REFRESH_SETTINGS = "refreshSettings"
    const val INTENT_REFRESH_WIDGETS_LIST = "refreshWidgetsList"
    const val INTENT_OPEN_BUS_STOP_EDIT = "editBusStop"
    const val INTENT_NO_INTERNET_CONNECTION = "noInternet"
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
