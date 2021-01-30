package co.teltech.base.data.remote

import co.teltech.base.vo.Employee
import retrofit2.Response
import retrofit2.http.GET


interface ApiService {
    @GET("/teltechiansFlat.json")
    suspend fun getEmployeeData(): Response<List<Employee>>
}