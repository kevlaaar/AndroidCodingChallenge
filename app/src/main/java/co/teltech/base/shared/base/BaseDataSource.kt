package co.teltech.base.shared.base

import com.google.gson.Gson
import co.teltech.base.vo.ErrorResponse
import retrofit2.HttpException
import retrofit2.Response

abstract class BaseDataSource {
    protected suspend fun <T> getResult(call: suspend () -> Response<T>): Resource<T> {
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) return Resource.Success(body)
            }
            return error(" ${response.code()} ${response.message()}")
        } catch (e: Exception) {
            if (e is HttpException) {
                val errorResponse = Gson().fromJson(
                    e.response()?.errorBody()?.charStream(),
                    ErrorResponse::class.java
                )
                return error(errorResponse?.message)
            }

            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(message: String?): Resource<T> {
        return Resource.Error("Network call has failed for the following reason: $message")
    }

}