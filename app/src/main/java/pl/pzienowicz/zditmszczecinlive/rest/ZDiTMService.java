package pl.pzienowicz.zditmszczecinlive.rest;

import java.util.List;

import pl.pzienowicz.zditmszczecinlive.model.Line;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ZDiTMService {

    @GET("json/linie.json")
    Call<List<Line>> listLines();
}
