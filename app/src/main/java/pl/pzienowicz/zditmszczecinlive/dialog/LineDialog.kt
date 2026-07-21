package pl.pzienowicz.zditmszczecinlive.dialog

import android.content.Context
import android.content.Intent
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

class LineDialog(context: Context) : AdaptiveSheetDialog(context) {
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

    init {
        binding = DialogLineBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setHeaderTextColors()

        val types = setOf(
            LineMatch(LineApiValue.TRAM, LineApiValue.DAY, LineApiValue.NORMAL, false) to Pair(binding.tramNormalTable, binding.tramExtraLabel),
            LineMatch(LineApiValue.BUS, LineApiValue.DAY, LineApiValue.NORMAL, false) to Pair(binding.busNormalTable, binding.busNormalLabel),
            LineMatch(LineApiValue.BUS, LineApiValue.DAY, LineApiValue.FAST, false) to Pair(binding.busExpressTable, binding.busExpressLabel),
            LineMatch(LineApiValue.BUS, LineApiValue.NIGHT, LineApiValue.NORMAL, false) to Pair(binding.busNightTable, binding.busNightLabel),
            LineMatch(LineApiValue.BUS, LineApiValue.DAY, LineApiValue.REPLACEMENT, false) to Pair(binding.busSubstituteTable, binding.busSubstituteLabel),
            LineMatch(LineApiValue.TRAM, LineApiValue.DAY, LineApiValue.REPLACEMENT, false) to Pair(binding.tramSubstituteTable, binding.tramSubstituteLabel),
            LineMatch(LineApiValue.TRAM, LineApiValue.DAY, LineApiValue.TOURIST, false) to Pair(binding.tramTouristicTable, binding.tramTouristicLabel),
            LineMatch(LineApiValue.BUS, LineApiValue.DAY, LineApiValue.TOURIST, false) to Pair(binding.busTouristicTable, binding.busTouristicLabel),
            LineMatch(LineApiValue.BUS, LineApiValue.DAY, LineApiValue.NORMAL, true) to Pair(binding.busNormalOnDemandTable, binding.busNormalOnDemandLabel),
            LineMatch(LineApiValue.TRAM, LineApiValue.DAY, LineApiValue.SPECIAL, false) to Pair(binding.tramExtraTable, binding.tramExtraLabel),
            LineMatch(LineApiValue.BUS, LineApiValue.DAY, LineApiValue.SPECIAL, false) to Pair(binding.busExtraTable, binding.busExtraLabel)
        )

        binding.clearFilterText.setOnClickListener { changeFilter(null) }

        currentLine = context.prefs.selectedLine

        if (!context.isNetworkAvailable) {
            context.showToast(R.string.no_internet)
            val intent = Intent(Config.INTENT_NO_INTERNET_CONNECTION)
            context.sendLocalBroadcast(intent)
            dismiss()
        }

        binding.progressBarHolder.visibility = View.VISIBLE

        val service = getRetrofit().create(ZDiTMService::class.java)
        val lines = service.listLines()
        lines.enqueue(object : Callback<Data<Line>?> {
            override fun onResponse(call: Call<Data<Line>?>, response: Response<Data<Line>?>) {
                binding.progressBarHolder.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    types.forEach {
                        drawLinesTable(
                            filterLines(response.body()?.items, it.first),
                            it.second
                        )
                    }
                } else {
                    context.showToast(R.string.lines_request_error)
                }
            }

            override fun onFailure(call: Call<Data<Line>?>, t: Throwable) {
                binding.progressBarHolder.visibility = View.GONE
                context.showToast(R.string.lines_request_error)
            }
        })
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

    private fun drawLinesTable(lines: List<Line>, pair: Pair<TableLayout, LinearLayout>) {
        if (lines.isEmpty()) {
            pair.first.visibility = View.GONE
            pair.second.visibility = View.GONE
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
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(2, 2, 2, 3)
                cellLayout.layoutParams = params
                val tv = TextView(context)
                tv.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT
                )
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                tv.gravity = Gravity.CENTER

                if (iterator < lines.size) {
                    val line = lines[iterator]
                    cellLayout.isClickable = true
                    cellLayout.isFocusable = true
                    cellLayout.id = line.id

                    if (line.highlighted) {
                        cellLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow))
                    } else {
                        cellLayout.setBackgroundResource(R.drawable.selector_line)
                    }

                    if (currentLine == line.id) {
                        tv.setTextColor(ContextCompat.getColor(context, R.color.red))
                    }

                    cellLayout.setOnClickListener { view: View ->
                        changeFilter(lines.first { it.id == view.id })
                    }
                    tv.text = line.number
                } else {
                    tv.setTextColor(ContextCompat.getColor(context, android.R.color.white))
                }
                cellLayout.addView(tv)
                row.addView(cellLayout)
                iterator++
            }
            pair.first.addView(row)
        }
    }

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
        val black = ContextCompat.getColor(context, R.color.black)
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
            binding.clearFilterText
        ).forEach { setTextColor(it, black) }
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
