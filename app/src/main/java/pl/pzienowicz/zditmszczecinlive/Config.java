package pl.pzienowicz.zditmszczecinlive;

public class Config {

    public static final String LOG_TAG = "Komunikacja";

    public static final String URL = "http://www.zditm.szczecin.pl/mapy/przystanki-i-pojazdy";
    public static final String LINE_URL = "http://www.zditm.szczecin.pl/mapy/linia,";
    public static final String BASE_URL = "http://www.zditm.szczecin.pl/";
    public static final String PREFERENCE_SHOW_DIALOG = "showDialog";
    public static final String PREFERENCE_FAVOURITE_MAP = "favouriteMap";

    public static final int LINES_PER_ROW = 5;
    public static final int ZONES_PER_ROW_LANDSCAPE = 9;

    public static final String PREFERENCE_SELECTED_LINE = "selectedLine";
    public static final String INTENT_LOAD_NEW_URL = "loadNewUrl";
    public static final String INTENT_REFRESH_SETTINGS = "refreshSettings";
    public static final String INTENT_NO_INTERNET_CONNETION = "noInternet";
    public static final String EXTRA_LINE_ID = "lineId";

    public static final String PREFERENCE_USE_LOCATION = "useLocation";
    public static final String PREFERENCE_ZOOM_MAP = "zoomMap";

}
