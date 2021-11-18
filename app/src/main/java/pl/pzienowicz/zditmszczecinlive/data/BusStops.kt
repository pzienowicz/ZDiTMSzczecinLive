package pl.pzienowicz.zditmszczecinlive.data

import android.content.Context

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.io.IOException
import java.io.InputStreamReader
import java.util.ArrayList

import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.model.BusStop

class BusStops(context: Context) {

    init {
        try {
            val `in` = context.resources.openRawResource(R.raw.slupki)
            val reader = InputStreamReader(`in`, "UTF-8")
            stops = Gson().fromJson(reader, object : TypeToken<ArrayList<BusStop>>() {}.type)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getByNumber(number: String): BusStop? {
        return stops.firstOrNull { it.numer == number }
    }

    fun getById(id: String): BusStop? {
        return stops.firstOrNull { it.id.toString() == id }
    }

    companion object {
        private var mInstance: BusStops? = null
        private var stops: List<BusStop> = ArrayList()

        fun getInstance(context: Context): BusStops {
            if (mInstance == null) {
                mInstance = BusStops(context)
            }
            return mInstance as BusStops
        }
    }
}
