package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.Window
import android.widget.*
import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.adapter.InfoListAdapter
import pl.pzienowicz.zditmszczecinlive.isNetworkAvailable
import pl.pzienowicz.zditmszczecinlive.model.Info
import pl.pzienowicz.zditmszczecinlive.rest.RetrofitClient
import pl.pzienowicz.zditmszczecinlive.rest.ZDiTMService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InfoDialog(context: Context) : Dialog(context) {

    private var progressBarHolder: FrameLayout? = null
    private var adapter: InfoListAdapter? = null
    private var noInfoTv: TextView? = null
    private val records = ArrayList<Info>()

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_info)

        noInfoTv = findViewById(R.id.noInfoTv)
        progressBarHolder = findViewById(R.id.progressBarHolder)
        adapter = InfoListAdapter(context, records)
        val listView = findViewById<ListView>(R.id.listView)
        listView.adapter = adapter

        if (!context.isNetworkAvailable) {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_LONG).show()

            val intent = Intent(Config.INTENT_NO_INTERNET_CONNETION)
            context.sendBroadcast(intent)

            dismiss()
        } else {
            progressBarHolder?.visibility = View.VISIBLE

            val service = RetrofitClient.getRetrofit().create(ZDiTMService::class.java)
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

            val contactUsBtn = findViewById<Button>(R.id.contactUsBtn)
            contactUsBtn.setOnClickListener {
                val emailIntent = Intent(
                    Intent.ACTION_SENDTO,
                    Uri.fromParts("mailto", context.getString(R.string.owner_email), null)
                )
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_title))
                context.startActivity(Intent.createChooser(emailIntent, "Wyślij email..."))
            }

            findViewById<Button>(R.id.ad1Button).setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(context.getString(R.string.latamy_url))
                context.startActivity(i)
            }

            findViewById<Button>(R.id.ad2Button).setOnClickListener {
                val callIntent = Intent(Intent.ACTION_DIAL)
                callIntent.data = Uri.parse(context.getString(R.string.owner_phone))
                context.startActivity(callIntent)
            }
        }
    }
}
