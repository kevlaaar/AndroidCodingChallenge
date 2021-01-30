package co.teltech.base.data.remote

import androidx.lifecycle.LiveData
import co.teltech.base.shared.base.BaseDataSource
import co.teltech.base.vo.Employee

class RemoteDataSource(private val apiService: ApiService) : BaseDataSource() {

    suspend fun getEmployeeData() = apiService.getEmployeeData()
}
