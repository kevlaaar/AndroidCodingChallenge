package co.teltech.base.ui.main.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import co.teltech.base.R
import co.teltech.base.shared.kotlin.setCircleGradientColor
import co.teltech.base.vo.Employee
import co.teltech.base.vo.GridEmployee
import com.bumptech.glide.Glide


class GridEmployeeAdapter(
    private val context: Context,
    private val gridEmployeeOnClickListener: GridEmployeeOnClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val itemCallback = object : DiffUtil.ItemCallback<GridEmployee>() {
        override fun areItemsTheSame(oldItem: GridEmployee, newItem: GridEmployee): Boolean {
            oldItem.headerTitle?.let { return oldItem.headerTitle == newItem.headerTitle } ?: run {
                return oldItem.employeeObject?.image == newItem.employeeObject?.image
            }
        }

        override fun areContentsTheSame(oldItem: GridEmployee, newItem: GridEmployee): Boolean {
            if(oldItem.employeeObject!= null){
                newItem.employeeObject?.imageBitmap?.let {
                    return false
                } ?: run {
                    return true
                }
            } else {
                return newItem.headerTitle == oldItem.headerTitle
            }
        }
    }
    private val differ: AsyncListDiffer<GridEmployee> = AsyncListDiffer(this, itemCallback)
    val HEADER_VIEW = 3
    val DATA_VIEW = 4
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == DATA_VIEW) {
            val view = layoutInflater.inflate(R.layout.item_recycler_employee, parent, false)
            GridEmployeeViewHolder(view)
        } else {
            val view = layoutInflater.inflate(R.layout.item_recycler_header, parent, false)
            EmployeeHeaderViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == DATA_VIEW) {
            val employee = differ.currentList[position].employeeObject
            holder as GridEmployeeViewHolder
            holder.bindEmployee(employee!!)
        } else {
            val header = differ.currentList[position].headerTitle
            holder as EmployeeHeaderViewHolder
            holder.bindHeader(header ?: "")
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return differ.currentList[position].viewType
    }

    fun setData(newListOfEmployees: ArrayList<GridEmployee>) {
        differ.submitList(newListOfEmployees.toMutableList())
    }

    inner class GridEmployeeViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        private val employeeImage: ImageView = itemView.findViewById(R.id.employeeImage)
        fun bindEmployee(employee: Employee) {
            Glide.with(context)
                .load(employee.imageBitmap)
                .circleCrop()
                .into(employeeImage)
            employee.backgroundColor?.let {
                employeeImage.setCircleGradientColor(
                    "#FFFFFF",
                    employee.backgroundColor,
                )
            }
            itemView.setOnClickListener{
                gridEmployeeOnClickListener.onGridEmployeeClick(adapterPosition)
            }
        }
    }

    inner class EmployeeHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerTitle: TextView = itemView.findViewById(R.id.headerTitle)
        fun bindHeader(title: String) {
            headerTitle.text = title.capitalize()
        }
    }

    interface GridEmployeeOnClickListener {
        fun onGridEmployeeClick(position: Int)
    }
}