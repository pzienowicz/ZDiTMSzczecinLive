package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.adapter.InfoListAdapter
import pl.pzienowicz.zditmszczecinlive.databinding.DialogInfoBinding
import pl.pzienowicz.zditmszczecinlive.isNetworkAvailable
import pl.pzienowicz.zditmszczecinlive.model.Data
import pl.pzienowicz.zditmszczecinlive.model.Info
import pl.pzienowicz.zditmszczecinlive.rest.RetrofitClient
import pl.pzienowicz.zditmszczecinlive.rest.ZDiTMService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.net.toUri

class InfoDialog(private val activity: Activity) : AdaptiveSheetDialog(activity) {

    private var adapter: InfoListAdapter
    private val records = ArrayList<Info>()
    private var binding: DialogInfoBinding

    init {
        binding = DialogInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = InfoListAdapter(activity, records)
        binding.listView.adapter = adapter

        if (!activity.isNetworkAvailable) {
            showError(R.string.no_internet)
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
                        showError(R.string.info_request_error)
                    }
                }

                override fun onFailure(call: Call<Data<Info>>, t: Throwable) {
                    binding.progressBarHolder.visibility = View.GONE
                    t.printStackTrace()
                    showError(R.string.info_request_error)
                }
            })

            binding.contactUsBtn.setOnClickListener {
                val emailIntent = Intent(
                    Intent.ACTION_SENDTO,
                    Uri.fromParts("mailto", activity.getString(R.string.owner_email), null)
                )
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.email_title))
                activity.startActivity(Intent.createChooser(emailIntent, "Wyślij email..."))
            }

            binding.ad2Button.setOnClickListener {
                val callIntent = Intent(Intent.ACTION_DIAL)
                callIntent.data = activity.getString(R.string.owner_phone).toUri()
                activity.startActivity(callIntent)
            }
        }
    }

    private fun showError(message: Int) {
        binding.errorText.setText(message)
        binding.errorText.visibility = View.VISIBLE
    }
}
