package pl.pzienowicz.zditmszczecinlive

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

val Context.prefs: Prefs
    get() = Prefs(this)

class Prefs (context: Context) {
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var selectedLine: Int
        get() = prefs.getInt(Config.PREFERENCE_SELECTED_LINE, 0)
        set(value) = prefs.edit().putInt(Config.PREFERENCE_SELECTED_LINE, value).apply()

    var favouriteMap: String
        get() = prefs.getString(Config.PREFERENCE_FAVOURITE_MAP, Config.URL) ?: Config.URL
        set(value) = prefs.edit().putString(Config.PREFERENCE_FAVOURITE_MAP, value).apply()

    var zoomMap: Boolean
        get() = prefs.getBoolean(Config.PREFERENCE_ZOOM_MAP, true)
        set(value) = prefs.edit().putBoolean(Config.PREFERENCE_ZOOM_MAP, value).apply()

    var useLocation: Boolean
        get() = prefs.getBoolean(Config.PREFERENCE_USE_LOCATION, true)
        set(value) = prefs.edit().putBoolean(Config.PREFERENCE_USE_LOCATION, value).apply()

    var showInitDialog: Boolean
        get() = prefs.getBoolean(Config.PREFERENCE_SHOW_DIALOG, true)
        set(value) = prefs.edit().putBoolean(Config.PREFERENCE_SHOW_DIALOG, value).apply()

    var refreshWidgets: Boolean
        get() = prefs.getBoolean(Config.PREFERENCE_WIDGETS_REFRESH, true)
        set(value) = prefs.edit().putBoolean(Config.PREFERENCE_WIDGETS_REFRESH, value).apply()

    var refreshWidgetsTime: String
        get() = prefs.getString(Config.PREFERENCE_WIDGETS_REFRESH_TIME, "30") ?: "30"
        set(value) = prefs.edit().putString(Config.PREFERENCE_WIDGETS_REFRESH_TIME, value).apply()

    fun putString(key: String, value: String?) = prefs.edit().putString(key, value).apply()
    fun getString(key: String): String? = prefs.getString(key, null)
}