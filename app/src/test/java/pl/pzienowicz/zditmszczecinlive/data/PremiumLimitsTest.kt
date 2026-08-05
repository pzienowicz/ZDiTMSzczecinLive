package pl.pzienowicz.zditmszczecinlive.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PremiumLimitsTest {

    private val limits = PremiumLimits(
        favouriteStopsLimit = 1,
        favouriteConnectionsLimit = 1,
        favouriteLinesLimit = 2
    )

    @Test
    fun canAddFavouriteStopAllowsFreeUserBelowLimit() {
        assertTrue(limits.canAddFavouriteStop(currentCount = 0, isPremiumUnlocked = false))
    }

    @Test
    fun canAddFavouriteStopBlocksFreeUserAtLimit() {
        assertFalse(limits.canAddFavouriteStop(currentCount = 1, isPremiumUnlocked = false))
    }

    @Test
    fun canAddFavouriteConnectionAllowsFreeUserBelowLimit() {
        assertTrue(limits.canAddFavouriteConnection(currentCount = 0, isPremiumUnlocked = false))
    }

    @Test
    fun canAddFavouriteConnectionBlocksFreeUserAtLimit() {
        assertFalse(limits.canAddFavouriteConnection(currentCount = 1, isPremiumUnlocked = false))
    }

    @Test
    fun canAddFavouriteLineAllowsFreeUserBelowLimit() {
        assertTrue(limits.canAddFavouriteLine(currentCount = 1, isPremiumUnlocked = false))
    }

    @Test
    fun canAddFavouriteLineBlocksFreeUserAtLimit() {
        assertFalse(limits.canAddFavouriteLine(currentCount = 2, isPremiumUnlocked = false))
    }

    @Test
    fun canAddFavouriteItemsAllowsPremiumUserAtLimits() {
        assertTrue(limits.canAddFavouriteStop(currentCount = 1, isPremiumUnlocked = true))
        assertTrue(limits.canAddFavouriteConnection(currentCount = 1, isPremiumUnlocked = true))
        assertTrue(limits.canAddFavouriteLine(currentCount = 2, isPremiumUnlocked = true))
    }

    @Test
    fun canAddFavouriteItemsAllowsPremiumUserAboveLimits() {
        assertTrue(limits.canAddFavouriteStop(currentCount = 5, isPremiumUnlocked = true))
        assertTrue(limits.canAddFavouriteConnection(currentCount = 5, isPremiumUnlocked = true))
        assertTrue(limits.canAddFavouriteLine(currentCount = 5, isPremiumUnlocked = true))
    }
}
