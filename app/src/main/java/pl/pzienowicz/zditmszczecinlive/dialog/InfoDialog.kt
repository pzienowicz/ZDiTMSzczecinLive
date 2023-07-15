package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.Window
import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.adapter.InfoListAdapter
import pl.pzienowicz.zditmszczecinlive.databinding.DialogInfoBinding
import pl.pzienowicz.zditmszczecinlive.isNetworkAvailable
import pl.pzienowicz.zditmszczecinlive.model.Data
import pl.pzienowicz.zditmszczecinlive.model.Info
import pl.pzienowicz.zditmszczecinlive.rest.RetrofitClient
import pl.pzienowicz.zditmszczecinlive.rest.ZDiTMService
import pl.pzienowicz.zditmszczecinlive.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InfoDialog(context: Context) : Dialog(context) {

    private var adapter: InfoListAdapter
    private val records = ArrayList<Info>()
    private var binding: DialogInfoBinding

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = InfoListAdapter(context, records)
        binding.listView.adapter = adapter

        if (!context.isNetworkAvailable) {
            context.showToast(R.string.no_internet)

            val intent = Intent(Config.INTENT_NO_INTERNET_CONNECTION)
            context.sendBroadcast(intent)

            dismiss()
        } else {
            binding.progressBarHolder.visibility = View.VISIBLE

            val service = RetrofitClient.getRetrofit().create(ZDiTMService::class.java)
            val lines = service.listInfo()
            lines.enqueue(object : Callback<Data<Info>> {
                override fun onResponse(call: Call<Data<Info>>, response: Response<Data<Info>>) {
                    binding.progressBarHolder.visibility = View.GONE

                    if (response.isSuccessful) {
                        records.clear()
                        response.body()?.let { records.addAll(it.items) }
                        adapter.notifyDataSetChanged()

                        if (records.isEmpty()) {
                            binding.noInfoTv.visibility = View.VISIBLE
                        }
                    } else {
                        context.showToast(R.string.info_request_error)
                    }
                }

                override fun onFailure(call: Call<Data<Info>>, t: Throwable) {
                    binding.progressBarHolder.visibility = View.GONE
                    t.printStackTrace()
                    context.showToast(R.string.info_request_error)
                }
            })

            binding.contactUsBtn.setOnClickListener {
                val emailIntent = Intent(
                    Intent.ACTION_SENDTO,
                    Uri.fromParts("mailto", context.getString(R.string.owner_email), null)
                )
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_title))
                context.startActivity(Intent.createChooser(emailIntent, "Wyślij email..."))
            }

            binding.ad2Button.setOnClickListener {
                val callIntent = Intent(Intent.ACTION_DIAL)
                callIntent.data = Uri.parse(context.getString(R.string.owner_phone))
                context.startActivity(callIntent)
            }
        }
    }
}
