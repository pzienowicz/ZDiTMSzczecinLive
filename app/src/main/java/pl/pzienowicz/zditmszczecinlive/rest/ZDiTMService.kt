package pl.pzienowicz.zditmszczecinlive.rest

import pl.pzienowicz.zditmszczecinlive.model.Board
import pl.pzienowicz.zditmszczecinlive.model.BusStop
import pl.pzienowicz.zditmszczecinlive.model.Data
import pl.pzienowicz.zditmszczecinlive.model.Info
import pl.pzienowicz.zditmszczecinlive.model.Line
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ZDiTMService {

    @GET("/api/v2/lines")
    fun listLines(): Call<Data<Line>>

    @GET("api/v2/timetable-change-descriptions")
    fun listInfo(): Call<Data<Info>>

    @GET("api/v2/departure-boards/{stopNumber}")
    fun getBoard(@Path("stopNumber") busStopNumber: String): Call<Board>

    @GET("api/v2/stops")
    fun listBusStops(): Call<Data<BusStop>>
}
