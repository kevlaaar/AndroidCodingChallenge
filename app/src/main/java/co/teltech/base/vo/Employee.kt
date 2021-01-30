package co.teltech.base.vo

import android.graphics.Bitmap
import co.teltech.base.shared.kotlin.createID
import java.io.Serializable

data class Employee(
    val id: Int = createID(),
    var department: String,
    var name: String,
    var surname: String,
    var title: String,
    var agency: String,
    var intro: String,
    var image: String,
    var description: String,
    var imageBitmap: Bitmap?,
    var altImageBitmap: Bitmap?,
    var imageMain: String,
    var backgroundColor: String
) : Serializable {
    fun getImageUrl(): String{
        return "https://teltech.co/images/members/$image.jpg"
    }
    fun getImageMainUrl(): String{
        return "https://teltech.co/images/members/$image-main.jpg"
    }
}