package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

import java.util.ArrayList

import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.Functions
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.adapter.InfoListAdapter
import pl.pzienowicz.zditmszczecinlive.model.Info
import pl.pzienowicz.zditmszczecinlive.rest.ZDiTMService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InfoDialog(context: Context) : Dialog(context) {

    private var progressBarHolder: FrameLayout? = null
    private var adapter: InfoListAdapter? = null
    private var noInfoTv: TextView? = null
    private val records = ArrayList<Info>()

    init {

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_info)

        noInfoTv = findViewById(R.id.noInfoTv) as TextView
        progressBarHolder = findViewById(R.id.progressBarHolder) as FrameLayout
        adapter = InfoListAdapter(context, records)
        val listView = findViewById(R.id.listView) as ListView
        listView.adapter = adapter

        val retrofit = Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        if (!Functions.isNetworkAvailable(context)) {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_LONG).show()

            val intent = Intent(Config.INTENT_NO_INTERNET_CONNETION)
            context.sendBroadcast(intent)

            dismiss()
        } else {
            progressBarHolder!!.visibility = View.VISIBLE

            val service = retrofit.create(ZDiTMService::class.java)
            val lines = service.listInfo()
            lines.enqueue(object : Callback<List<Info>> {
                override fun onResponse(call: Call<List<Info>>, response: Response<List<Info>>) {
                    progressBarHolder!!.visibility = View.GONE

                    if (response.isSuccessful) {
                        records.clear()
                        response.body()?.let { records.addAll(it) }
                        adapter!!.notifyDataSetChanged()

                        if (records.isEmpty()) {
                            noInfoTv!!.visibility = View.VISIBLE
                        }
                    } else {
                        Toast.makeText(context, R.string.info_request_error, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<List<Info>>, t: Throwable) {
                    progressBarHolder!!.visibility = View.GONE
                    t.printStackTrace()
                    Toast.makeText(context, R.string.info_request_error, Toast.LENGTH_LONG).show()
                }
            })

            val contactUsBtn = findViewById(R.id.contactUsBtn) as Button
            contactUsBtn.setOnClickListener {
                val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "zienowicz.pawel@gmail.com", null))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Reklama - Komunikacja Miejska Szczecin")
                context.startActivity(Intent.createChooser(emailIntent, "Wyślij email..."))
            }
        }
    }
}
