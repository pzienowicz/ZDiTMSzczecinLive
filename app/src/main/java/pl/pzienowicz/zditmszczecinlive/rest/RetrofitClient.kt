package pl.pzienowicz.zditmszczecinlive.rest

import okhttp3.OkHttpClient
import pl.pzienowicz.zditmszczecinlive.Config
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val USER_AGENT = "Komunikacja Miejska Szczecin (aplikacja nieoficjalna)"

    fun getRetrofit(): Retrofit {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .header("User-Agent", USER_AGENT)
                .build()

            chain.proceed(request)
        }
        httpClient.readTimeout(60, TimeUnit.SECONDS)
        httpClient.connectTimeout(60, TimeUnit.SECONDS)

        return Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
    }
}
