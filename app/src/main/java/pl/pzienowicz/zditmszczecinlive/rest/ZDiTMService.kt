package pl.pzienowicz.zditmszczecinlive.rest

import pl.pzienowicz.zditmszczecinlive.model.Board
import pl.pzienowicz.zditmszczecinlive.model.BusStop
import pl.pzienowicz.zditmszczecinlive.model.Info
import pl.pzienowicz.zditmszczecinlive.model.Line
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ZDiTMService {

    @GET("json/linie.json")
    fun listLines(): Call<List<Line>>

    @GET("json/zmianyrj.inc.php")
    fun listInfo(): Call<List<Info>>

    @GET("json/tablica.inc.php")
    fun getBoard(@Query("slupek") busStopNumber: String): Call<Board>

    @GET("json/slupki.inc.php")
    fun listBusStops(): Call<List<BusStop>>
}
