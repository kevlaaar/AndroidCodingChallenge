package co.teltech.base.ui.main

import android.app.Application
import android.graphics.Bitmap
import android.os.Parcelable
import androidx.lifecycle.*
import co.teltech.base.data.repo.UserRepository
import co.teltech.base.shared.kotlin.toBitmap
import co.teltech.base.vo.Employee
import co.teltech.base.vo.Team
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.URL
import java.util.*
import kotlin.collections.set


class MainFragmentViewModel(
    application: Application,
    private val userRepo: UserRepository
) :
    AndroidViewModel(application) {
    var employeeObject: Employee? = null
    var teamList = MutableLiveData<List<Team>>()
    var listState: Parcelable? = null
    var selectedEmployeeBackgroundColor: String? = null
    var selectedEmployeeImageUrl: String? = null
    var selectedEmployeeListPosition: Int? = null
    var colorHashMap: HashMap<String, String> = hashMapOf()
    private var colorList: ArrayList<String> = arrayListOf("#FF33B5E5", "#FFAA66CC", "#4CAF50",
            "#FFFFBB33", "#FFFF4444", "#7A1235", "#FF9933CC", "#01770D", "#FFFF8800",
            "#FFCC0000", "#CDDC39", "#F44336", "#2C00CC")
    var usedColorList: ArrayList<String> = arrayListOf()
    var employeeList = liveData<List<Employee>?> {
        val response = userRepo.getEmployeeData()
        if (response.isSuccessful) {
            val list = response.body()
            val teamList: ArrayList<Team> = arrayListOf()
            list?.let {
                var count = 8
                it.forEach { employee ->
                    count -= 1
                    val bitmaps = getImagesBitmapsFromUrl(employee.getImageUrl())
                    employee.imageBitmap = bitmaps[0]!!
                    employee.altImageBitmap = bitmaps[1]!!
                    colorHashMap[employee.department]?.let { colorInt ->
                        employee.backgroundColor = colorInt
                        Timber.e("VEC IMA ${employee.department} i dajem boju $colorInt")
                    } ?: run {
                        Timber.e("NIJE BILO ${employee.department}")
                        val randomColor = getRandomColorString()
                        colorHashMap[employee.department] = randomColor
                        teamList.add(Team(randomColor, employee.department))
                        employee.backgroundColor = randomColor
                    }
                    if (count == 0) {
                        emit(it)
                        count = 8
                    }
                }
                emit(it)
                populateTeamList(teamList)
            }
        }
    }
    private fun populateTeamList(teamListArg: List<Team>){
            teamList.value = teamListArg
    }
    private fun getRandomColorString(): String {
        val randomColor = colorList[Random().nextInt(colorList.size)]
        usedColorList.add(randomColor)
        colorList.remove(randomColor)
        return randomColor
    }

    private suspend fun getImagesBitmapsFromUrl(imageUrl: String): Array<Bitmap?> =
        withContext(Dispatchers.IO) {
            val url = URL(imageUrl)
            val fullBitmap = url.toBitmap() as Bitmap
            val bitmaps = arrayOfNulls<Bitmap>(2)
            bitmaps[0] =
                Bitmap.createBitmap(fullBitmap, 0, 0, fullBitmap.width / 2, fullBitmap.height)
            bitmaps[1] = Bitmap.createBitmap(
                fullBitmap,
                fullBitmap.width / 2,
                0,
                fullBitmap.width / 2,
                fullBitmap.height
            )
            Timber.e(" BITMAPNOZE ${bitmaps[0]} i ${bitmaps[1]}")
            bitmaps
        }
}