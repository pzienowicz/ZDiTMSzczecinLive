package pl.pzienowicz.zditmszczecinlive.data

import pl.pzienowicz.zditmszczecinlive.Config

class PremiumLimits(
    private val favouriteStopsLimit: Int = Config.FAVOURITE_STOPS_FREE_LIMIT,
    private val favouriteConnectionsLimit: Int = Config.FAVOURITE_CONNECTIONS_FREE_LIMIT,
    private val favouriteLinesLimit: Int = Config.FAVOURITE_LINES_FREE_LIMIT
) {

    fun canAddFavouriteStop(currentCount: Int, isPremiumUnlocked: Boolean): Boolean =
        canAdd(currentCount, favouriteStopsLimit, isPremiumUnlocked)

    fun canAddFavouriteConnection(currentCount: Int, isPremiumUnlocked: Boolean): Boolean =
        canAdd(currentCount, favouriteConnectionsLimit, isPremiumUnlocked)

    fun canAddFavouriteLine(currentCount: Int, isPremiumUnlocked: Boolean): Boolean =
        canAdd(currentCount, favouriteLinesLimit, isPremiumUnlocked)

    private fun canAdd(
        currentCount: Int,
        freeLimit: Int,
        isPremiumUnlocked: Boolean
    ): Boolean =
        isPremiumUnlocked || currentCount < freeLimit
}
