package co.teltech.base

import android.app.Application
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import co.teltech.base.di.*
import co.teltech.base.shared.GlideApp
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import okhttp3.OkHttpClient
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber
import java.io.InputStream

@Suppress("unused")
class BaseApp : Application() {

    private val sharedOkHttpClient: OkHttpClient by inject()
    companion object {
        lateinit var firebaseAnalytics: FirebaseAnalytics
    }
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        // Initialize Koin
        startKoin {
            androidContext(this@BaseApp)
            androidLogger(Level.ERROR)
            modules(
                listOf(
                    networkModule,
                    dataModule,
                    viewModelModule,
                    miscModule
                )
            )
        }
        // Initialize Glide with shared instance of OkHttpClient
        GlideApp.get(this)
            .registry
            .replace(
                GlideUrl::class.java,
                InputStream::class.java,
                OkHttpUrlLoader.Factory(sharedOkHttpClient)
            )

        // Initialize timber
        Timber.plant(Timber.DebugTree())
    }
}