package co.teltech.base.shared.util

import androidx.recyclerview.widget.DiffUtil
import co.teltech.base.vo.Employee

class EmployeesDiffUtilCallback(
    private val oldList: List<Employee>,
    private val newList: List<Employee>
) : DiffUtil.Callback() {
    companion object {
        const val IMAGE_BITMAP_KEY = "image_bitmap_key"
        const val ALT_IMAGE_BITMAP_KEY = "alt_image_bitmap_key"
    }
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].image == newList[newItemPosition].image
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.imageBitmap == newItem.imageBitmap
    }

//    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
//        val oldItem = oldList[oldItemPosition]
//        val newItem = newList[newItemPosition]
//
//        val changeMap = mutableMapOf<String, Any>()
//        val isImageLoaded = oldItem.imageBitmap == newItem.imageBitmap
//        val isAltImageLoaded = oldItem.altImageBitmap == newItem.altImageBitmap
//
//        if(!isAltImageLoaded){
//            changeMap[ALT_IMAGE_BITMAP_KEY] = newItem.altImageBitmap
//        }
//        if(!isImageLoaded){
//            changeMap[IMAGE_BITMAP_KEY] = newItem.imageBitmap
//        }
//
//        return changeMap
//    }
}