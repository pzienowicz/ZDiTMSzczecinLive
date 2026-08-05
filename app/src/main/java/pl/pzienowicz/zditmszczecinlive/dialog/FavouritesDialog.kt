package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.billing.GooglePlayBillingClient
import pl.pzienowicz.zditmszczecinlive.data.FavouriteDepartureFormatter
import pl.pzienowicz.zditmszczecinlive.data.FavouritesRepository
import pl.pzienowicz.zditmszczecinlive.data.PremiumLimits
import pl.pzienowicz.zditmszczecinlive.databinding.DialogFavouritesBinding
import pl.pzienowicz.zditmszczecinlive.isNetworkAvailable
import pl.pzienowicz.zditmszczecinlive.model.Board
import pl.pzienowicz.zditmszczecinlive.model.BusStop
import pl.pzienowicz.zditmszczecinlive.model.FavouriteConnection
import pl.pzienowicz.zditmszczecinlive.model.FavouriteLine
import pl.pzienowicz.zditmszczecinlive.model.FavouriteStop
import pl.pzienowicz.zditmszczecinlive.rest.RetrofitClient
import pl.pzienowicz.zditmszczecinlive.rest.ZDiTMService
import pl.pzienowicz.zditmszczecinlive.setFullWidth
import pl.pzienowicz.zditmszczecinlive.showBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavouritesDialog(
    private val activity: Activity,
    private val onLineSelected: (FavouriteLine) -> Unit
) : AdaptiveSheetDialog(activity) {

    private val binding = DialogFavouritesBinding.inflate(layoutInflater)
    private val repository = FavouritesRepository(activity)
    private val premiumLimits = PremiumLimits()
    private var premiumUnlockedAfterPurchase = false
    private val billingClient = GooglePlayBillingClient(
        activity = activity,
        onInitialized = {},
        onPurchased = {
            premiumUnlockedAfterPurchase = true
            activity.showBar(R.string.payment_success)
        }
    )
    private val departureFormatter = FavouriteDepartureFormatter(
        FavouriteDepartureFormatter.AndroidStrings(activity)
    )

    init {
        setContentView(binding.root)

        binding.addFavouriteStopButton.setOnClickListener {
            if (isFavouriteStopsLimitReached()) {
                openPremiumDialog()
                return@setOnClickListener
            }
            openAddStopDialog()
        }
        binding.addFavouriteConnectionButton.setOnClickListener {
            if (isFavouriteConnectionsLimitReached()) {
                openPremiumDialog()
                return@setOnClickListener
            }
            openAddConnectionStopDialog()
        }
        renderFavouriteStops()
        renderFavouriteConnections()
        renderFavouriteLines()
    }

    private fun openAddStopDialog() {
        val dialog = BusStopDialog(
            activity = activity,
            onSelected = { busStop ->
                if (isFavouriteStopsLimitReached()) {
                    openPremiumDialog()
                    return@BusStopDialog
                }
                repository.addFavouriteStop(busStop)
                renderFavouriteStops()
            },
            currentBusStop = null
        )
        dialog.setFullWidth()
        dialog.show()
    }

    private fun openAddConnectionStopDialog() {
        val dialog = BusStopDialog(
            activity = activity,
            onSelected = { busStop ->
                openDeparturePicker(busStop)
            },
            currentBusStop = null
        )
        dialog.setFullWidth()
        dialog.show()
    }

    private fun openDeparturePicker(busStop: BusStop) {
        if (!activity.isNetworkAvailable) {
            showError(R.string.no_internet)
            return
        }

        val service = RetrofitClient.getRetrofit().create(ZDiTMService::class.java)
        service.getBoard(busStop.number, CONNECTION_PICKER_DEPARTURES_LIMIT).enqueue(object : Callback<Board> {
            override fun onResponse(call: Call<Board>, response: Response<Board>) {
                if (!response.isSuccessful) {
                    showError(
                        if (response.code() == HTTP_TOO_MANY_REQUESTS) {
                            R.string.departures_rate_limit_error
                        } else {
                            R.string.departures_request_error
                        }
                    )
                    return
                }

                val departures = response.body()
                    ?.departures
                    ?.distinctBy { it.line_number to it.direction }
                    .orEmpty()
                if (departures.isEmpty()) {
                    showError(R.string.no_connections_to_add)
                    return
                }

                val dialog = createConnectionPickerDialog(busStop, departures)
                dialog.setFullWidth()
                dialog.show()
            }

            override fun onFailure(call: Call<Board>, t: Throwable) {
                showError(R.string.departures_request_error)
            }
        })
    }

    private fun createConnectionPickerDialog(
        busStop: BusStop,
        departures: List<Board.Departure>
    ): AdaptiveSheetDialog {
        val dialog = AdaptiveSheetDialog(activity)
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(15), dp(15), dp(15), dp(15))
        }
        container.addView(
            View(context).apply {
                setBackgroundResource(R.drawable.bg_bottom_sheet_handle)
                layoutParams = LinearLayout.LayoutParams(dp(40), dp(4)).apply {
                    gravity = android.view.Gravity.CENTER_HORIZONTAL
                    bottomMargin = dp(12)
                }
            }
        )
        container.addView(createTitleText(context.getString(R.string.select_favourite_connection)))

        departures.forEach { departure ->
            container.addView(
                createConnectionPickerCard(departure) {
                    if (isFavouriteConnectionsLimitReached()) {
                        openPremiumDialog()
                        return@createConnectionPickerCard
                    }
                    repository.addFavouriteConnection(busStop, departure)
                    renderFavouriteConnections()
                    dialog.dismiss()
                }
            )
        }

        dialog.setContentView(container)
        return dialog
    }

    private fun createConnectionPickerCard(
        departure: Board.Departure,
        onClick: () -> Unit
    ): View =
        createClickableCard(
            title = "${departure.line_number} - ${departure.direction}",
            subtitle = departureFormatter.format(departure),
            onClick = onClick
        )

    private fun renderFavouriteStops() {
        binding.favouriteStopsContainer.removeAllViews()
        val stops = repository.getFavouriteStops()
        binding.emptyFavouriteStopsText.visibility = if (stops.isEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }

        stops.forEach { stop ->
            binding.favouriteStopsContainer.addView(createStopCard(stop))
        }
    }

    private fun renderFavouriteConnections() {
        binding.favouriteConnectionsContainer.removeAllViews()
        val connections = repository.getFavouriteConnections()
        binding.emptyFavouriteConnectionsText.visibility = if (connections.isEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }

        connections.forEach { connection ->
            binding.favouriteConnectionsContainer.addView(createConnectionCard(connection))
        }
    }

    private fun renderFavouriteLines() {
        binding.favouriteLinesContainer.removeAllViews()
        val lines = repository.getFavouriteLines()
        binding.emptyFavouriteLinesText.visibility = if (lines.isEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }

        lines.chunked(LINES_PER_ROW).forEach { rowLines ->
            val row = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            rowLines.forEach { line ->
                row.addView(createLineCard(line))
            }

            repeat(LINES_PER_ROW - rowLines.size) {
                row.addView(createLineSpacer())
            }

            binding.favouriteLinesContainer.addView(row)
        }
    }

    private fun createStopCard(stop: FavouriteStop): View {
        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            isClickable = true
            isFocusable = true
            setBackgroundResource(R.drawable.bg_snackbar)
            setPadding(dp(12), dp(10), dp(8), dp(10))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(8)
            }
            setOnClickListener {
                openScheduleBoard(stop)
            }
        }

        val textContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }
        textContainer.addView(
            createTitleText("${stop.stopName} ${stop.stopNumber}")
        )
        val departuresText = createSubtitleText(context.getString(R.string.loading_departures))
        textContainer.addView(departuresText)
        loadDepartures(stop, departuresText)

        val deleteButton = ImageButton(context).apply {
            setImageResource(R.drawable.ic_delete_24dp)
            background = null
            setBackgroundColor(Color.TRANSPARENT)
            contentDescription = context.getString(R.string.remove_favourite_stop)
            setPadding(dp(8), dp(8), dp(8), dp(8))
            layoutParams = LinearLayout.LayoutParams(dp(48), dp(48))
            setOnClickListener {
                repository.removeFavouriteStop(stop.stopNumber)
                renderFavouriteStops()
            }
        }

        row.addView(textContainer)
        row.addView(deleteButton)
        return row
    }

    private fun createBaseRow(onClick: () -> Unit): LinearLayout =
        LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            isClickable = true
            isFocusable = true
            setBackgroundResource(R.drawable.bg_snackbar)
            setPadding(dp(12), dp(10), dp(8), dp(10))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(8)
            }
            setOnClickListener { onClick() }
        }

    private fun createConnectionCard(connection: FavouriteConnection): View {
        val row = createBaseRow {
            openScheduleBoard(connection.toBusStop())
        }

        val textContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }
        textContainer.addView(createTitleText("${connection.lineNumber} - ${connection.direction}"))
        val departureText = createSubtitleText(context.getString(R.string.loading_departures))
        textContainer.addView(departureText)
        loadConnectionDeparture(connection, departureText)

        val deleteButton = ImageButton(context).apply {
            setImageResource(R.drawable.ic_delete_24dp)
            background = null
            setBackgroundColor(Color.TRANSPARENT)
            contentDescription = context.getString(R.string.remove_favourite_connection)
            setPadding(dp(8), dp(8), dp(8), dp(8))
            layoutParams = LinearLayout.LayoutParams(dp(48), dp(48))
            setOnClickListener {
                repository.removeFavouriteConnection(connection)
                renderFavouriteConnections()
            }
        }

        row.addView(textContainer)
        row.addView(deleteButton)
        return row
    }

    private fun createLineCard(line: FavouriteLine): View {
        val card = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            isClickable = true
            isFocusable = true
            setBackgroundResource(R.drawable.bg_snackbar)
            setPadding(dp(10), dp(10), dp(4), dp(10))
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                setMargins(dp(2), dp(2), dp(2), dp(6))
            }
            setOnClickListener {
                onLineSelected(line)
                dismiss()
            }
        }

        val title = createTitleText(line.title).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }
        val deleteButton = ImageButton(context).apply {
            setImageResource(R.drawable.ic_delete_24dp)
            background = null
            setBackgroundColor(Color.TRANSPARENT)
            contentDescription = context.getString(R.string.remove_favourite_line)
            setPadding(dp(8), dp(8), dp(8), dp(8))
            layoutParams = LinearLayout.LayoutParams(dp(40), dp(40))
            setOnClickListener {
                repository.removeFavouriteLine(line.url)
                renderFavouriteLines()
            }
        }

        card.addView(title)
        card.addView(deleteButton)
        return card
    }

    private fun createLineSpacer(): View =
        View(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

    private fun openScheduleBoard(stop: FavouriteStop) {
        openScheduleBoard(stop.toBusStop())
    }

    private fun openScheduleBoard(busStop: BusStop) {
        val dialog = ScheduleBoardDialog(
            context,
            busStop
        )
        dialog.setFullWidth()
        dialog.show()
    }

    private fun FavouriteStop.toBusStop(): BusStop =
        BusStop(id = stopId, number = stopNumber, name = stopName)

    private fun FavouriteConnection.toBusStop(): BusStop =
        BusStop(id = stopId, number = stopNumber, name = stopName)

    private fun loadDepartures(stop: FavouriteStop, departuresText: TextView) {
        if (!activity.isNetworkAvailable) {
            departuresText.setText(R.string.no_internet)
            return
        }

        val service = RetrofitClient.getRetrofit().create(ZDiTMService::class.java)
        service.getBoard(stop.stopNumber, DEPARTURES_LIMIT).enqueue(object : Callback<Board> {
            override fun onResponse(call: Call<Board>, response: Response<Board>) {
                if (!response.isSuccessful) {
                    departuresText.setText(
                        if (response.code() == HTTP_TOO_MANY_REQUESTS) {
                            R.string.departures_rate_limit_error
                        } else {
                            R.string.departures_request_error
                        }
                    )
                    return
                }

                departuresText.text = response.body()
                    ?.departures
                    ?.take(DEPARTURES_LIMIT)
                    ?.joinToString(", ") { departure -> departureFormatter.format(departure) }
                    ?.takeIf { it.isNotBlank() }
                    ?: context.getString(R.string.no_departures)
            }

            override fun onFailure(call: Call<Board>, t: Throwable) {
                departuresText.setText(R.string.departures_request_error)
            }
        })
    }

    private fun loadConnectionDeparture(connection: FavouriteConnection, departureText: TextView) {
        if (!activity.isNetworkAvailable) {
            departureText.setText(R.string.no_internet)
            return
        }

        val service = RetrofitClient.getRetrofit().create(ZDiTMService::class.java)
        service.getBoard(connection.stopNumber, CONNECTION_DEPARTURES_LIMIT).enqueue(object : Callback<Board> {
            override fun onResponse(call: Call<Board>, response: Response<Board>) {
                if (!response.isSuccessful) {
                    departureText.setText(
                        if (response.code() == HTTP_TOO_MANY_REQUESTS) {
                            R.string.departures_rate_limit_error
                        } else {
                            R.string.departures_request_error
                        }
                    )
                    return
                }

                val departure = response.body()
                    ?.departures
                    ?.firstOrNull {
                        it.line_number == connection.lineNumber &&
                            it.direction == connection.direction
                    }

                departureText.text = if (departure != null) {
                    "${connection.stopName}, ${departureFormatter.format(departure)}"
                } else {
                    context.getString(R.string.no_departures)
                }
            }

            override fun onFailure(call: Call<Board>, t: Throwable) {
                departureText.setText(R.string.departures_request_error)
            }
        })
    }

    private fun createCard(title: String, subtitle: String): View {
        val card = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.drawable.bg_snackbar)
            setPadding(dp(12), dp(10), dp(12), dp(10))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(8)
            }
        }

        card.addView(createTitleText(title))
        card.addView(createSubtitleText(subtitle))
        return card
    }

    private fun createClickableCard(title: String, subtitle: String, onClick: () -> Unit): View =
        createCard(title, subtitle).apply {
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
        }

    private fun createTitleText(text: String): TextView =
        TextView(context).apply {
            this.text = text
            setTextColor(ContextCompat.getColor(context, R.color.app_text))
            setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 15f)
            setTypeface(typeface, Typeface.BOLD)
        }

    private fun createSubtitleText(text: String): TextView =
        TextView(context).apply {
            this.text = text
            setTextColor(ContextCompat.getColor(context, R.color.app_text_secondary))
            setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 13f)
        }

    private fun dp(value: Int): Int =
        (value * context.resources.displayMetrics.density).toInt()

    private fun showError(message: Int) {
        activity.showBar(message)
    }

    private fun isFavouriteStopsLimitReached(): Boolean =
        !premiumLimits.canAddFavouriteStop(repository.getFavouriteStops().size, isPremiumUnlocked())

    private fun isFavouriteConnectionsLimitReached(): Boolean =
        !premiumLimits.canAddFavouriteConnection(repository.getFavouriteConnections().size, isPremiumUnlocked())

    private fun isPremiumUnlocked(): Boolean =
        premiumUnlockedAfterPurchase || billingClient.isPremiumUnlocked()

    private fun openPremiumDialog() {
        val dialog = PremiumDialog(activity) {
            premiumUnlockedAfterPurchase = true
        }
        dialog.setFullWidth()
        dialog.show()
    }

    private companion object {
        const val DEPARTURES_LIMIT = 3
        const val CONNECTION_DEPARTURES_LIMIT = 10
        const val CONNECTION_PICKER_DEPARTURES_LIMIT = 20
        const val HTTP_TOO_MANY_REQUESTS = 429
        const val LINES_PER_ROW = 2
    }
}
