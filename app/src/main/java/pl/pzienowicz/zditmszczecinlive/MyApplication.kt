package pl.pzienowicz.zditmszczecinlive

import android.app.Application
import android.content.Context

import org.acra.ACRA
import org.acra.annotation.AcraCore
import org.acra.annotation.AcraHttpSender

@AcraCore(buildConfigClass = BuildConfig::class)
@AcraHttpSender(
        httpMethod = org.acra.sender.HttpSender.Method.PUT,
        uri = Config.ACRA_URL,
        basicAuthLogin = Config.ACRA_USER,
        basicAuthPassword = Config.ACRA_PASS
)
class MyApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

        // The following line triggers the initialization of ACRA
        ACRA.init(this)
    }
}
