package co.teltech.base.di

import com.google.gson.GsonBuilder
import co.teltech.base.BuildConfig
import co.teltech.base.data.remote.ApiService
import co.teltech.base.data.remote.AuthorizationInterceptor
import co.teltech.base.data.remote.RemoteDataSource
import co.teltech.base.data.remote.TokenRefreshAuthenticator
import co.teltech.base.data.repo.UserRepository
import co.teltech.base.shared.util.PreferenceCache
import co.teltech.base.shared.util.SettingsManager
import co.teltech.base.ui.MainViewModel
import co.teltech.base.ui.main.MainFragmentViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {

    single { AuthorizationInterceptor(get()) }

    single { TokenRefreshAuthenticator() }

    single {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(get<AuthorizationInterceptor>())
            .authenticator(get<TokenRefreshAuthenticator>())

        if (BuildConfig.DEBUG)
            okHttpClient.addInterceptor(
                HttpLoggingInterceptor()
                    .apply { level = HttpLoggingInterceptor.Level.BODY })

        okHttpClient.build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(get())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
            .create(ApiService::class.java)
    }
}

val dataModule = module {
    single { RemoteDataSource(get()) }
    single { UserRepository(get(), get<AuthorizationInterceptor>(), get()) }
}

val viewModelModule = module {
    viewModel { MainViewModel() }
    viewModel { MainFragmentViewModel(androidApplication(), get()) }
}
val miscModule = module {
    single { PreferenceCache(androidContext()) }

    single { SettingsManager(get()) }

    single {
        GsonBuilder()
            .create()
    }
}