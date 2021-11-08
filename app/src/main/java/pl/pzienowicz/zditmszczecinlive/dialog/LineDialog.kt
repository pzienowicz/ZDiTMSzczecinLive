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
import kotlinx.android.synthetic.main.dialog_line.*
import pl.pzienowicz.zditmszczecinlive.*
import pl.pzienowicz.zditmszczecinlive.model.Line
import pl.pzienowicz.zditmszczecinlive.rest.RetrofitClient.getRetrofit
import pl.pzienowicz.zditmszczecinlive.rest.ZDiTMService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LineDialog(context: Context) : Dialog(context) {
    private val currentLine: Int

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_line)

        clearFilterText.setOnClickListener { changeFilter(0) }

        currentLine = context.prefs.getInt(Config.PREFERENCE_SELECTED_LINE, 0)

        if (!context.isNetworkAvailable) {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_LONG).show()
            val intent = Intent(Config.INTENT_NO_INTERNET_CONNETION)
            context.sendBroadcast(intent)
            dismiss()
        }

        progressBarHolder.visibility = View.VISIBLE

        val service = getRetrofit().create(ZDiTMService::class.java)
        val lines = service.listLines()
        lines.enqueue(object : Callback<List<Line>?> {
            override fun onResponse(call: Call<List<Line>?>, response: Response<List<Line>?>) {
                progressBarHolder.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    drawLinesTable(
                        filterLines(response.body(), "tdz"),
                        tramNormalTable,
                        tramNormalLabel
                    )
                    drawLinesTable(
                        filterLines(response.body(), "adz"),
                        busNormalTable,
                        busNormalLabel
                    )
                    drawLinesTable(
                        filterLines(response.body(), "adp"),
                        busExpressTable,
                        busExpressLabel
                    )
                    drawLinesTable(
                        filterLines(response.body(), "anz"),
                        busNightTable,
                        busNightLabel
                    )
                    drawLinesTable(
                        filterLines(response.body(), "ada"),
                        busSubstituteTable,
                        busSubstituteLabel
                    )
                    drawLinesTable(
                        filterLines(response.body(), "tda"),
                        tramSubstituteTable,
                        tramSubstituteLabel
                    )
                    drawLinesTable(
                        filterLines(response.body(), "tdt"),
                        tramTouristicTable,
                        tramTouristicLabel
                    )
                    drawLinesTable(
                        filterLines(response.body(), "adt"),
                        busTouristicTable,
                        busTouristicLabel
                    )
                    drawLinesTable(
                        filterLines(response.body(), "adz1"),
                        busNormalOnDemandTable,
                        busNormalOnDemandLabel
                    )
                    drawLinesTable(
                        filterLines(response.body(), "tdd"),
                        tramExtraTable,
                        tramExtraLabel
                    )
                    drawLinesTable(
                        filterLines(response.body(), "add"),
                        busExtraTable,
                        busExtraLabel
                    )
                } else {
                    Toast.makeText(context, R.string.lines_request_error, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Line>?>, t: Throwable) {
                progressBarHolder.visibility = View.GONE
                Toast.makeText(context, R.string.lines_request_error, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun filterLines(lines: List<Line>?, type: String): List<Line> {
        return lines?.filter { it.type == type } ?: emptyList()
    }

    private fun drawLinesTable(lines: List<Line>, layout: TableLayout, label: LinearLayout) {
        if (lines.isEmpty()) {
            layout.visibility = View.GONE
            label.visibility = View.GONE
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
            layout.addView(row)
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