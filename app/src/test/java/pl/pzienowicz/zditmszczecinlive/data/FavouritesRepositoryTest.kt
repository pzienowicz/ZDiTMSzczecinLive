package pl.pzienowicz.zditmszczecinlive.data

import org.junit.Assert.assertEquals
import org.junit.Test
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

    private class FakeStorage(
        override var favouriteStopsJson: String = "[]"
    ) : FavouritesRepository.Storage
}
