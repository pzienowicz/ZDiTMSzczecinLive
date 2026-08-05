package pl.pzienowicz.zditmszczecinlive.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import pl.pzienowicz.zditmszczecinlive.model.Board
import pl.pzienowicz.zditmszczecinlive.model.BusStop
import pl.pzienowicz.zditmszczecinlive.model.FavouriteConnection
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
            override var favouriteConnectionsJson: String
                get() = context.prefs.favouriteConnectionsJson
                set(value) {
                    context.prefs.favouriteConnectionsJson = value
                }
        }
    )

    interface Storage {
        var favouriteStopsJson: String
        var favouriteConnectionsJson: String
    }

    private val gson = Gson()
    private val favouriteStopListType = object : TypeToken<List<FavouriteStop>>() {}.type
    private val favouriteConnectionListType = object : TypeToken<List<FavouriteConnection>>() {}.type

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

    fun getFavouriteConnections(): List<FavouriteConnection> =
        runCatching {
            gson.fromJson<List<FavouriteConnection>>(
                storage.favouriteConnectionsJson,
                favouriteConnectionListType
            )
        }.getOrNull().orEmpty()

    fun addFavouriteConnection(
        busStop: BusStop,
        departure: Board.Departure
    ) {
        val currentConnections = getFavouriteConnections()
        if (
            currentConnections.any {
                it.stopNumber == busStop.number &&
                    it.lineNumber == departure.line_number &&
                    it.direction == departure.direction
            }
        ) {
            return
        }

        saveFavouriteConnections(
            currentConnections + FavouriteConnection(
                stopId = busStop.id,
                stopNumber = busStop.number,
                stopName = busStop.name,
                lineNumber = departure.line_number,
                direction = departure.direction
            )
        )
    }

    fun removeFavouriteConnection(connection: FavouriteConnection) {
        saveFavouriteConnections(
            getFavouriteConnections().filterNot {
                it.stopNumber == connection.stopNumber &&
                    it.lineNumber == connection.lineNumber &&
                    it.direction == connection.direction
            }
        )
    }

    private fun saveFavouriteStops(stops: List<FavouriteStop>) {
        storage.favouriteStopsJson = gson.toJson(stops)
    }

    private fun saveFavouriteConnections(connections: List<FavouriteConnection>) {
        storage.favouriteConnectionsJson = gson.toJson(connections)
    }
}
