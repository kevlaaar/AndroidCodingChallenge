package co.teltech.base.data.remote

import co.teltech.base.data.repo.EmployeeRepository
import co.teltech.base.shared.kotlin.signWithToken
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber

class TokenRefreshAuthenticator : Authenticator {
    var employeeRepository: EmployeeRepository? = null

    private val Response.retryCount: Int
        get() {
            var currentResponse = priorResponse
            var result = 0
            while (currentResponse != null) {
                result++
                currentResponse = currentResponse.priorResponse
            }
            return result
        }

    override fun authenticate(route: Route?, response: Response): Request? = when {
        response.retryCount > 2 -> null
        else -> response.createSignedRequest()
    }

    private fun Response.createSignedRequest(): Request? = try {
        val accessToken = employeeRepository?.refreshToken()
        request.signWithToken(accessToken)
    } catch (error: Throwable) {
        Timber.e("Failed to resign request")
        null
    }
}