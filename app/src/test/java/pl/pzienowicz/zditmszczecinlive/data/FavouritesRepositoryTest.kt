package pl.pzienowicz.zditmszczecinlive.data

import org.junit.Assert.assertEquals
import org.junit.Test
import pl.pzienowicz.zditmszczecinlive.model.Board
import pl.pzienowicz.zditmszczecinlive.model.BusStop

class FavouritesRepositoryTest {

    @Test
    fun addFavouriteStopStoresStop() {
        val repository = FavouritesRepository(FakeStorage())

        repository.addFavouriteStop(BusStop(id = 123, number = "11512", name = "Plac Rodla"))

        assertEquals(
            listOf(
                "11512" to "Plac Rodla"
            ),
            repository.getFavouriteStops().map { it.stopNumber to it.stopName }
        )
    }

    @Test
    fun addFavouriteStopIgnoresDuplicateStopNumber() {
        val repository = FavouritesRepository(FakeStorage())

        repository.addFavouriteStop(BusStop(id = 123, number = "11512", name = "Plac Rodla"))
        repository.addFavouriteStop(BusStop(id = 456, number = "11512", name = "Plac Rodla Dublet"))

        assertEquals(1, repository.getFavouriteStops().size)
        assertEquals("Plac Rodla", repository.getFavouriteStops().first().stopName)
    }

    @Test
    fun removeFavouriteStopDeletesMatchingStopNumber() {
        val repository = FavouritesRepository(FakeStorage())
        repository.addFavouriteStop(BusStop(id = 123, number = "11512", name = "Plac Rodla"))
        repository.addFavouriteStop(BusStop(id = 456, number = "10821", name = "Brama Portowa"))

        repository.removeFavouriteStop("11512")

        assertEquals(
            listOf("10821"),
            repository.getFavouriteStops().map { it.stopNumber }
        )
    }

    @Test
    fun getFavouriteStopsReturnsEmptyListForInvalidJson() {
        val repository = FavouritesRepository(FakeStorage(favouriteStopsJson = "{invalid"))

        assertEquals(emptyList<Any>(), repository.getFavouriteStops())
    }

    @Test
    fun addFavouriteConnectionStoresConnection() {
        val repository = FavouritesRepository(FakeStorage())

        repository.addFavouriteConnection(
            BusStop(id = 123, number = "11512", name = "Plac Rodla"),
            departure(line = "75", direction = "Osiedle Bukowe")
        )

        assertEquals(
            listOf("75" to "Osiedle Bukowe"),
            repository.getFavouriteConnections().map { it.lineNumber to it.direction }
        )
    }

    @Test
    fun addFavouriteConnectionIgnoresDuplicateStopLineAndDirection() {
        val repository = FavouritesRepository(FakeStorage())
        val busStop = BusStop(id = 123, number = "11512", name = "Plac Rodla")

        repository.addFavouriteConnection(busStop, departure(line = "75", direction = "Osiedle Bukowe"))
        repository.addFavouriteConnection(busStop, departure(line = "75", direction = "Osiedle Bukowe"))

        assertEquals(1, repository.getFavouriteConnections().size)
    }

    @Test
    fun removeFavouriteConnectionDeletesMatchingConnection() {
        val repository = FavouritesRepository(FakeStorage())
        val busStop = BusStop(id = 123, number = "11512", name = "Plac Rodla")
        repository.addFavouriteConnection(busStop, departure(line = "75", direction = "Osiedle Bukowe"))
        repository.addFavouriteConnection(busStop, departure(line = "8", direction = "Gumience"))

        repository.removeFavouriteConnection(repository.getFavouriteConnections().first())

        assertEquals(
            listOf("8"),
            repository.getFavouriteConnections().map { it.lineNumber }
        )
    }

    private fun departure(line: String, direction: String): Board.Departure =
        Board.Departure(
            line_number = line,
            direction = direction,
            time_real = 7,
            time_scheduled = null
        )

    private class FakeStorage(
        override var favouriteStopsJson: String = "[]",
        override var favouriteConnectionsJson: String = "[]"
    ) : FavouritesRepository.Storage
}
