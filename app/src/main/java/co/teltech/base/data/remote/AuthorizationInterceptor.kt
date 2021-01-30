package co.teltech.base.data.remote

import co.teltech.base.shared.util.PreferenceCache
import co.teltech.base.shared.kotlin.signWithToken
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor(private val prefsCache: PreferenceCache) : Interceptor,
    AccessTokenListener {
    private var accessToken: String = ""
        set(value) {
            prefsCache.accessToken = value
            field = value
        }

    override fun intercept(chain: Interceptor.Chain): Response {
        val signedRequest = chain.request().signWithToken(accessToken)
        return chain.proceed(signedRequest)
    }

    override fun onChanged(newToken: String) {
        accessToken = newToken
    }
}

interface AccessTokenListener {
    fun onChanged(newToken: String)
}