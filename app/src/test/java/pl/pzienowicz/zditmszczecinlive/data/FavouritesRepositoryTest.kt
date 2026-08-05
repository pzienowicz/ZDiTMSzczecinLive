package pl.pzienowicz.zditmszczecinlive.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.pzienowicz.zditmszczecinlive.model.Board
import pl.pzienowicz.zditmszczecinlive.model.BusStop
import pl.pzienowicz.zditmszczecinlive.model.FavouriteLine

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

    @Test
    fun addFavouriteLineStoresLine() {
        val repository = FavouritesRepository(FakeStorage())

        repository.addFavouriteLine(FavouriteLine(title = "Linia 75", url = "https://example.com/75"))

        assertEquals(
            listOf("Linia 75" to "https://example.com/75"),
            repository.getFavouriteLines().map { it.title to it.url }
        )
    }

    @Test
    fun addFavouriteLineIgnoresDuplicateUrl() {
        val repository = FavouritesRepository(FakeStorage())

        repository.addFavouriteLine(FavouriteLine(title = "Linia 75", url = "https://example.com/75"))
        repository.addFavouriteLine(FavouriteLine(title = "Linia 75 dublet", url = "https://example.com/75"))

        assertEquals(1, repository.getFavouriteLines().size)
        assertEquals("Linia 75", repository.getFavouriteLines().first().title)
    }

    @Test
    fun removeFavouriteLineDeletesMatchingUrl() {
        val repository = FavouritesRepository(FakeStorage())
        repository.addFavouriteLine(FavouriteLine(title = "Linia 75", url = "https://example.com/75"))
        repository.addFavouriteLine(FavouriteLine(title = "Linia 8", url = "https://example.com/8"))

        repository.removeFavouriteLine("https://example.com/75")

        assertEquals(
            listOf("https://example.com/8"),
            repository.getFavouriteLines().map { it.url }
        )
    }

    @Test
    fun isFavouriteLineChecksUrl() {
        val repository = FavouritesRepository(FakeStorage())
        repository.addFavouriteLine(FavouriteLine(title = "Linia 75", url = "https://example.com/75"))

        assertTrue(repository.isFavouriteLine("https://example.com/75"))
        assertFalse(repository.isFavouriteLine("https://example.com/8"))
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
        override var favouriteConnectionsJson: String = "[]",
        override var favouriteLinesJson: String = "[]"
    ) : FavouritesRepository.Storage
}
