package co.teltech.base.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import co.teltech.base.data.remote.AccessTokenListener
import co.teltech.base.data.remote.RemoteDataSource
import co.teltech.base.data.remote.TokenRefreshAuthenticator
import co.teltech.base.shared.base.Resource

class EmployeeRepository(
    private val remoteDataSource: RemoteDataSource,
    private val accessTokenListener: AccessTokenListener,
    authenticator: TokenRefreshAuthenticator
) {
    init {
        authenticator.employeeRepository = this
    }

    suspend fun getEmployeeData() = remoteDataSource.getEmployeeData()

    fun refreshToken(): String {
        val newToken = ""
        accessTokenListener.onChanged(newToken)
        return newToken
    }

    private fun <T> retrieve(networkCall: suspend () -> Resource<T>): LiveData<Resource<T>> =
        liveData(Dispatchers.Default) {
            emit(Resource.Loading())
            val response = networkCall()
            emit(response)
        }
}