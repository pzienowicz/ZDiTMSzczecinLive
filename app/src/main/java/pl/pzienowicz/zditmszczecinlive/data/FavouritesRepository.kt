package pl.pzienowicz.zditmszczecinlive.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import pl.pzienowicz.zditmszczecinlive.model.BusStop
import pl.pzienowicz.zditmszczecinlive.model.FavouriteStop
import pl.pzienowicz.zditmszczecinlive.prefs

class FavouritesRepository(
    private val storage: Storage
) {

    constructor(context: Context) : this(
        object : Storage {
            override var favouriteStopsJson: String
                get() = context.prefs.favouriteStopsJson
                set(value) {
                    context.prefs.favouriteStopsJson = value
                }
        }
    )

    interface Storage {
        var favouriteStopsJson: String
    }

    private val gson = Gson()
    private val favouriteStopListType = object : TypeToken<List<FavouriteStop>>() {}.type

    fun getFavouriteStops(): List<FavouriteStop> =
        runCatching {
            gson.fromJson<List<FavouriteStop>>(
                storage.favouriteStopsJson,
                favouriteStopListType
            )
        }.getOrNull().orEmpty()

    fun addFavouriteStop(busStop: BusStop) {
        val currentStops = getFavouriteStops()
        if (currentStops.any { it.stopNumber == busStop.number }) {
            return
        }

        saveFavouriteStops(
            currentStops + FavouriteStop(
                stopId = busStop.id,
                stopNumber = busStop.number,
                stopName = busStop.name
            )
        )
    }

    fun removeFavouriteStop(stopNumber: String) {
        saveFavouriteStops(getFavouriteStops().filterNot { it.stopNumber == stopNumber })
    }

    private fun saveFavouriteStops(stops: List<FavouriteStop>) {
        storage.favouriteStopsJson = gson.toJson(stops)
    }
}
