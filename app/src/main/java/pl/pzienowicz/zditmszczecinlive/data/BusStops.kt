package pl.pzienowicz.zditmszczecinlive.data

import android.content.Context

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.io.IOException
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.HashMap

import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.model.BusStop

class BusStops(context: Context) {

    init {
        stops = HashMap()
        var list = ArrayList<BusStop>()

        try {
            val `in` = context.resources.openRawResource(R.raw.slupki)
            val reader = InputStreamReader(`in`, "UTF-8")
            list = Gson().fromJson(reader, object : TypeToken<ArrayList<BusStop>>() {

            }.type)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        for (busStop in list) {
            stops[busStop.numer!!] = busStop
            stopsById[busStop.id!!] = busStop
        }
    }

    fun getByNumber(number: String): BusStop? {
        return stops.get(number)
    }

    fun getById(id: String): BusStop? {
        return stopsById[id]
    }

    companion object {

        private var mInstance: BusStops? = null
        private var stops: HashMap<String, BusStop> = HashMap()
        private val stopsById: HashMap<String, BusStop> = HashMap()

        fun getInstance(context: Context): BusStops {
            if (mInstance == null) {
                mInstance = BusStops(context)
            }
            return mInstance as BusStops
        }
    }

}
