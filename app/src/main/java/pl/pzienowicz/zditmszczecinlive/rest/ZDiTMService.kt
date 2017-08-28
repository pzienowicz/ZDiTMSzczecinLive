package pl.pzienowicz.zditmszczecinlive.rest

import pl.pzienowicz.zditmszczecinlive.model.Info
import pl.pzienowicz.zditmszczecinlive.model.Line
import retrofit2.Call
import retrofit2.http.GET

interface ZDiTMService {

    @GET("json/linie.json")
    fun listLines(): Call<List<Line>>

    @GET("json/zmianyrj.inc.php")
    fun listInfo(): Call<List<Info>>
}
