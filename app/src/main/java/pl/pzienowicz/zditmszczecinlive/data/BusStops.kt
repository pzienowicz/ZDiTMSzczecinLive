package pl.pzienowicz.zditmszczecinlive.data

import android.content.Context

import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.model.BusStop
import pl.pzienowicz.zditmszczecinlive.model.Data
import pl.pzienowicz.zditmszczecinlive.rest.RetrofitClient
import pl.pzienowicz.zditmszczecinlive.rest.ZDiTMService
import pl.pzienowicz.zditmszczecinlive.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BusStops(val context: Context) {

    private fun load(
        onLoaded: () -> Unit,
        onError: (() -> Unit)? = null
    ) {
        val service = RetrofitClient.getRetrofit().create(ZDiTMService::class.java)
        val lines = service.listBusStops()
        lines.enqueue(object : Callback<Data<BusStop>> {
            override fun onResponse(call: Call<Data<BusStop>>, response: Response<Data<BusStop>>) {
                if (response.isSuccessful) {
                    stops.clear()
                    response.body()?.let { stops.addAll(it.items) }
                    onLoaded()
                } else {
                    showError(onError)
                }
            }

            override fun onFailure(call: Call<Data<BusStop>>, t: Throwable) {
                showError(onError)
            }
        })
    }

    fun loadByNumber(
        number: String,
        onError: (() -> Unit)? = null,
        callback: (BusStop?) -> Unit
    ) {
        if(stops.isEmpty()) {
            load(onLoaded = {
                callback(getByNumber(number))
            }, onError = onError)
        } else {
            callback(getByNumber(number))
        }
    }

    fun loadByIdOrNumber(
        id: String,
        number: String,
        onError: (() -> Unit)? = null,
        callback: (BusStop?) -> Unit
    ) {
        if(stops.isEmpty()) {
            load(onLoaded = {
                callback(getById(id) ?: getByNumber(number))
            }, onError = onError)
        } else {
            callback(getById(id) ?: getByNumber(number))
        }
    }

    private fun showError(onError: (() -> Unit)?) {
        if (onError != null) {
            onError()
        } else {
            context.showToast(R.string.stops_request_error)
        }
    }

    private fun getByNumber(number: String): BusStop? {
        return stops.firstOrNull { it.number == number }
    }

    private fun getById(id: String): BusStop? {
        return stops.firstOrNull { it.id.toString() == id }
    }

    companion object {
        private var mInstance: BusStops? = null
        private var stops: MutableList<BusStop> = mutableListOf()

        fun getInstance(context: Context): BusStops {
            if (mInstance == null) {
                mInstance = BusStops(context)
            }
            return mInstance as BusStops
        }
    }
}
