package pl.pzienowicz.zditmszczecinlive.dialog

import android.content.Intent
import android.app.Activity
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import pl.pzienowicz.zditmszczecinlive.*
import pl.pzienowicz.zditmszczecinlive.databinding.DialogLineBinding
import pl.pzienowicz.zditmszczecinlive.model.Data
import pl.pzienowicz.zditmszczecinlive.model.Line
import pl.pzienowicz.zditmszczecinlive.rest.RetrofitClient.getRetrofit
import pl.pzienowicz.zditmszczecinlive.rest.ZDiTMService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LineDialog(private val activity: Activity) : AdaptiveSheetDialog(activity) {
    private val currentLine: Int
    private var binding: DialogLineBinding

    private object LineApiValue {
        const val BUS = "BUS"
        const val TRAM = "TRAM"
        const val DAY = "DAY"
        const val NIGHT = "NIGHT"
        const val NORMAL = "NORMAL"
        const val FAST = "FAST"
        const val REPLACEMENT = "REPLACEMENT"
        const val SPECIAL = "SPECIAL"
        const val TOURIST = "TOURIST"
    }

    data class LineMatch(
        val vehicleType: String,
        val type: String,
        val subtype: String,
        val onDemand: Boolean
    )

    data class LineSection(
        val match: LineMatch,
        val table: TableLayout,
        val label: LinearLayout,
        val colorRes: Int
    )

    init {
        binding = DialogLineBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setHeaderTextColors()

        val types = listOf(
            LineSection(LineMatch(LineApiValue.TRAM, LineApiValue.DAY, LineApiValue.NORMAL, false), binding.tramNormalTable, binding.tramNormalLabel, R.color.line_tram),
            LineSection(LineMatch(LineApiValue.BUS, LineApiValue.DAY, LineApiValue.NORMAL, false), binding.busNormalTable, binding.busNormalLabel, R.color.line_bus),
            LineSection(LineMatch(LineApiValue.BUS, LineApiValue.DAY, LineApiValue.FAST, false), binding.busExpressTable, binding.busExpressLabel, R.color.line_bus_express),
            LineSection(LineMatch(LineApiValue.BUS, LineApiValue.NIGHT, LineApiValue.NORMAL, false), binding.busNightTable, binding.busNightLabel, R.color.line_bus_night),
            LineSection(LineMatch(LineApiValue.BUS, LineApiValue.NIGHT, LineApiValue.REPLACEMENT, false), binding.busNightSubstituteTable, binding.busNightSubstituteLabel, R.color.line_bus_night),
            LineSection(LineMatch(LineApiValue.BUS, LineApiValue.DAY, LineApiValue.REPLACEMENT, false), binding.busSubstituteTable, binding.busSubstituteLabel, R.color.line_bus),
            LineSection(LineMatch(LineApiValue.TRAM, LineApiValue.DAY, LineApiValue.REPLACEMENT, false), binding.tramSubstituteTable, binding.tramSubstituteLabel, R.color.line_tram),
            LineSection(LineMatch(LineApiValue.TRAM, LineApiValue.DAY, LineApiValue.TOURIST, false), binding.tramTouristicTable, binding.tramTouristicLabel, R.color.line_tram_touristic),
            LineSection(LineMatch(LineApiValue.BUS, LineApiValue.DAY, LineApiValue.TOURIST, false), binding.busTouristicTable, binding.busTouristicLabel, R.color.line_bus_touristic),
            LineSection(LineMatch(LineApiValue.BUS, LineApiValue.DAY, LineApiValue.NORMAL, true), binding.busNormalOnDemandTable, binding.busNormalOnDemandLabel, R.color.line_bus),
            LineSection(LineMatch(LineApiValue.TRAM, LineApiValue.DAY, LineApiValue.SPECIAL, false), binding.tramExtraTable, binding.tramExtraLabel, R.color.line_tram),
            LineSection(LineMatch(LineApiValue.BUS, LineApiValue.DAY, LineApiValue.SPECIAL, false), binding.busExtraTable, binding.busExtraLabel, R.color.line_bus)
        )

        binding.clearFilterText.setOnClickListener { changeFilter(null) }

        currentLine = activity.prefs.selectedLine

        if (!activity.isNetworkAvailable) {
            showError(R.string.no_internet)
        } else {
            binding.progressBarHolder.visibility = View.VISIBLE

            val service = getRetrofit().create(ZDiTMService::class.java)
            val lines = service.listLines()
            lines.enqueue(object : Callback<Data<Line>?> {
                override fun onResponse(call: Call<Data<Line>?>, response: Response<Data<Line>?>) {
                    binding.progressBarHolder.visibility = View.GONE

                    if (response.isSuccessful && response.body() != null) {
                        types.forEach {
                            drawLinesTable(
                                filterLines(response.body()?.items, it.match),
                                it
                            )
                        }
                    } else {
                        showError(R.string.lines_request_error)
                    }
                }

                override fun onFailure(call: Call<Data<Line>?>, t: Throwable) {
                    binding.progressBarHolder.visibility = View.GONE
                    showError(R.string.lines_request_error)
                }
            })
        }
    }

    private fun showError(message: Int) {
        binding.errorText.setText(message)
        binding.errorText.visibility = View.VISIBLE
    }

    private fun filterLines(lines: List<Line>?, match: LineMatch): List<Line> {
        return lines?.filter {
            it.vehicle_type == match.vehicleType &&
            it.type == match.type &&
            it.subtype == match.subtype &&
            it.on_demand == match.onDemand
        }?.sortedWith(compareBy {
            when {
                it.number.toIntOrNull() != null -> it.number.toInt()
                else -> it.number
            }
        }) ?: emptyList()
    }

    private fun drawLinesTable(lines: List<Line>, section: LineSection) {
        if (lines.isEmpty()) {
            section.table.visibility = View.GONE
            section.label.visibility = View.GONE
            return
        }

        var iterator = 0
        var linesPerRow = Config.LINES_PER_ROW

        if (context.isLandscape) {
            linesPerRow = Config.LINES_PER_ROW_LANDSCAPE
        }

        val rows = if (lines.size % linesPerRow == 0) {
            lines.size / linesPerRow
        } else {
            lines.size / linesPerRow + 1
        }

        for (i in 1..rows) {
            val row = TableRow(context)
            row.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            for (j in 1..linesPerRow) {
                val cellLayout = LinearLayout(context)
                val params = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 1f
                    setMargins(dp(4), dp(3), dp(4), dp(5))
                }
                cellLayout.layoutParams = params
                cellLayout.orientation = LinearLayout.VERTICAL

                if (iterator < lines.size) {
                    val line = lines[iterator]
                    cellLayout.isClickable = true
                    cellLayout.isFocusable = true
                    cellLayout.id = line.id

                    cellLayout.setOnClickListener { view: View ->
                        changeFilter(lines.first { it.id == view.id })
                    }
                    cellLayout.addView(createLineTopBar(section.colorRes))
                    cellLayout.addView(createLineNumberView(line))
                }
                row.addView(cellLayout)
                iterator++
            }
            section.table.addView(row)
        }
    }

    private fun createLineTopBar(colorRes: Int): View =
        View(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(3)
            )
            setBackgroundColor(ContextCompat.getColor(context, colorRes))
        }

    private fun createLineNumberView(line: Line): TextView =
        TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(37)
            )
            background = createLineTileBackground(line.highlighted)
            elevation = dp(2).toFloat()
            gravity = Gravity.CENTER
            includeFontPadding = false
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(
                ContextCompat.getColor(
                    context,
                    if (currentLine == line.id) R.color.red else R.color.black
                )
            )
            text = line.number
        }

    private fun createLineTileBackground(highlighted: Boolean): GradientDrawable =
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(ContextCompat.getColor(context, if (highlighted) R.color.yellow else R.color.white))
            setStroke(dp(1), ContextCompat.getColor(context, R.color.app_surface_stroke))
        }

    private fun dp(value: Int): Int =
        (value * context.resources.displayMetrics.density).toInt()

    private fun changeFilter(line: Line?) {
        val intent = Intent(Config.INTENT_LOAD_NEW_URL)
        line?.let {
            context.prefs.selectedLine = line.id
            intent.putExtra(Config.EXTRA_LINE_ID, "${line.id}/${line.number}")
        }
        context.sendLocalBroadcast(intent)
        dismiss()
    }

    private fun setHeaderTextColors() {
        val textColor = ContextCompat.getColor(context, R.color.app_text)
        listOf(
            binding.tramNormalLabel,
            binding.tramExtraLabel,
            binding.tramSubstituteLabel,
            binding.tramTouristicLabel,
            binding.busNormalLabel,
            binding.busNormalOnDemandLabel,
            binding.busExpressLabel,
            binding.busTouristicLabel,
            binding.busSubstituteLabel,
            binding.busExtraLabel,
            binding.busNightLabel,
            binding.busNightSubstituteLabel,
            binding.clearFilterText
        ).forEach { setTextColor(it, textColor) }
    }

    private fun setTextColor(view: View, color: Int) {
        if (view is TextView) {
            view.setTextColor(color)
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                setTextColor(view.getChildAt(i), color)
            }
        }
    }
}
