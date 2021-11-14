package pl.pzienowicz.zditmszczecinlive

import android.app.Application
import android.content.Context

import org.acra.config.httpSender
import org.acra.ktx.initAcra

class MyApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

        initAcra {
            //core configuration:
            buildConfigClass = BuildConfig::class.java
            httpSender {
                httpMethod = org.acra.sender.HttpSender.Method.PUT
                uri = Config.ACRA_URL
                basicAuthLogin = Config.ACRA_USER
                basicAuthPassword = Config.ACRA_PASS
            }
        }
    }
}
