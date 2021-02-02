package co.teltech.base.data.remote

import co.teltech.base.shared.base.BaseDataSource

class RemoteDataSource(private val apiService: ApiService) : BaseDataSource() {

    suspend fun getEmployeeData() = apiService.getEmployeeData()
}
