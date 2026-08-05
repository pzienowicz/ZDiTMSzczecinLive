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
import pl.pzienowicz.zditmszczecinlive.data.FavouriteDepartureFormatter
import pl.pzienowicz.zditmszczecinlive.data.FavouritesRepository
import pl.pzienowicz.zditmszczecinlive.databinding.DialogFavouritesBinding
import pl.pzienowicz.zditmszczecinlive.isNetworkAvailable
import pl.pzienowicz.zditmszczecinlive.model.Board
import pl.pzienowicz.zditmszczecinlive.model.BusStop
import pl.pzienowicz.zditmszczecinlive.model.FavouriteStop
import pl.pzienowicz.zditmszczecinlive.rest.RetrofitClient
import pl.pzienowicz.zditmszczecinlive.rest.ZDiTMService
import pl.pzienowicz.zditmszczecinlive.setFullWidth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavouritesDialog(private val activity: Activity) : AdaptiveSheetDialog(activity) {

    private val binding = DialogFavouritesBinding.inflate(layoutInflater)
    private val repository = FavouritesRepository(activity)
    private val departureFormatter = FavouriteDepartureFormatter(
        FavouriteDepartureFormatter.AndroidStrings(activity)
    )

    private data class FavouriteConnectionMock(
        val line: String,
        val stop: String,
        val direction: String,
        val departure: String
    )

    private val favouriteConnections = listOf(
        FavouriteConnectionMock("75", "Plac Rodla", "Osiedle Bukowe", "za 7 min"),
        FavouriteConnectionMock("8", "Turkusowa", "Gumience", "za 11 min")
    )

    init {
        setContentView(binding.root)

        binding.addFavouriteStopButton.setOnClickListener {
            openAddStopDialog()
        }

        renderFavouriteStops()
        favouriteConnections.forEach { connection ->
            binding.favouriteConnectionsContainer.addView(
                createCard(
                    title = "${connection.line} - ${connection.direction}",
                    subtitle = "${connection.stop}, ${connection.departure}"
                )
            )
        }
    }

    private fun openAddStopDialog() {
        val dialog = BusStopDialog(
            activity = activity,
            onSelected = { busStop ->
                repository.addFavouriteStop(busStop)
                renderFavouriteStops()
            },
            currentBusStop = null
        )
        dialog.setFullWidth()
        dialog.show()
    }

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

    private fun openScheduleBoard(stop: FavouriteStop) {
        val dialog = ScheduleBoardDialog(
            context,
            BusStop(
                id = stop.stopId,
                number = stop.stopNumber,
                name = stop.stopName
            )
        )
        dialog.setFullWidth()
        dialog.show()
    }

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

    private companion object {
        const val DEPARTURES_LIMIT = 3
        const val HTTP_TOO_MANY_REQUESTS = 429
    }
}
