package pl.pzienowicz.zditmszczecinlive.rest

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.pzienowicz.zditmszczecinlive.Config
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    fun getRetrofit(): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(interceptor) // <-- this is the importan

        return Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
//                .client(httpClient.build())
                .build()
    }
}