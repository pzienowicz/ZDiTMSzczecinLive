package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.*
import androidx.core.content.ContextCompat
import pl.pzienowicz.zditmszczecinlive.*
import pl.pzienowicz.zditmszczecinlive.databinding.DialogLineBinding
import pl.pzienowicz.zditmszczecinlive.model.Line
import pl.pzienowicz.zditmszczecinlive.rest.RetrofitClient.getRetrofit
import pl.pzienowicz.zditmszczecinlive.rest.ZDiTMService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LineDialog(context: Context) : Dialog(context) {
    private val currentLine: Int
    private var binding: DialogLineBinding

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogLineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val types = setOf(
            "tdz" to Pair(binding.tramNormalTable, binding.tramExtraLabel),
            "adz" to Pair(binding.busNormalTable, binding.busNormalLabel),
            "adp" to Pair(binding.busExpressTable, binding.busExpressLabel),
            "anz" to Pair(binding.busNightTable, binding.busNightLabel),
            "ada" to Pair(binding.busSubstituteTable, binding.busSubstituteLabel),
            "tda" to Pair(binding.tramSubstituteTable, binding.tramSubstituteLabel),
            "tdt" to Pair(binding.tramTouristicTable, binding.tramTouristicLabel),
            "adt" to Pair(binding.busTouristicTable, binding.busTouristicLabel),
            "adz1" to Pair(binding.busNormalOnDemandTable, binding.busNormalOnDemandLabel),
            "tdd" to Pair(binding.tramExtraTable, binding.tramExtraLabel),
            "add" to Pair(binding.busExpressTable, binding.busExpressLabel)
        )

        binding.clearFilterText.setOnClickListener { changeFilter(0) }

        currentLine = context.prefs.getInt(Config.PREFERENCE_SELECTED_LINE, 0)

        if (!context.isNetworkAvailable) {
            context.showToast(R.string.no_internet)
            val intent = Intent(Config.INTENT_NO_INTERNET_CONNETION)
            context.sendBroadcast(intent)
            dismiss()
        }

        binding.progressBarHolder.visibility = View.VISIBLE

        val service = getRetrofit().create(ZDiTMService::class.java)
        val lines = service.listLines()
        lines.enqueue(object : Callback<List<Line>?> {
            override fun onResponse(call: Call<List<Line>?>, response: Response<List<Line>?>) {
                binding.progressBarHolder.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    types.forEach {
                        drawLinesTable(
                            filterLines(response.body(), it.first),
                            it.second
                        )
                    }
                } else {
                    context.showToast(R.string.lines_request_error)
                }
            }

            override fun onFailure(call: Call<List<Line>?>, t: Throwable) {
                binding.progressBarHolder.visibility = View.GONE
                context.showToast(R.string.lines_request_error)
            }
        })
    }

    private fun filterLines(lines: List<Line>?, type: String): List<Line> {
        return lines?.filter { it.type == type } ?: emptyList()
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

                    if (line.isChanged()) {
                        cellLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow))
                    } else {
                        cellLayout.setBackgroundResource(R.drawable.selector_line)
                    }

                    if (currentLine == line.id) {
                        tv.setTextColor(ContextCompat.getColor(context, R.color.red))
                    }

                    cellLayout.setOnClickListener { view: View -> changeFilter(view.id) }
                    tv.text = line.name
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

    private fun changeFilter(id: Int) {
        context.prefs.edit().putInt(Config.PREFERENCE_SELECTED_LINE, id).apply()
        val intent = Intent(Config.INTENT_LOAD_NEW_URL)
        intent.putExtra(Config.EXTRA_LINE_ID, id)
        context.sendBroadcast(intent)
        dismiss()
    }
}