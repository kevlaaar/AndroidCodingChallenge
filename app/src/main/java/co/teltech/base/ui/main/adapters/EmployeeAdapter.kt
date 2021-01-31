package co.teltech.base.ui.main.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import co.teltech.base.R
import co.teltech.base.shared.kotlin.setCircleGradientColor
import co.teltech.base.vo.Employee
import com.bumptech.glide.Glide
import timber.log.Timber


class EmployeeAdapter(
    private val context: Context,
    private var employeeOnClickListener: EmployeeOnClickListener
) : RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {
    private val itemCallback = object : DiffUtil.ItemCallback<Employee>() {
        override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            newItem.imageBitmap?.let{
                return false
            } ?: run {
                return true
            }
        }
    }

    private val differ: AsyncListDiffer<Employee> = AsyncListDiffer(this, itemCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_recycler_employee, parent, false)
        return EmployeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = differ.currentList[position]
        holder.bindEmployee(employee)
    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    override fun getItemId(position: Int): Long {
        return differ.currentList[position].id.toLong()
    }

    fun setData(newListOfEmployees: List<Employee>) {
        differ.submitList(newListOfEmployees.toMutableList())
    }

    inner class EmployeeViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        private val employeeImage: ImageView = itemView.findViewById(R.id.employeeImage)
        private var colorHashMap: HashMap<String, String> = hashMapOf()
        fun bindEmployee(employee: Employee) {
            Timber.e("BINDING ${employee.imageBitmap}")
            Glide.with(context)
                .load(employee.imageBitmap)
                .circleCrop()
                .into(employeeImage)
            employee.backgroundColor?.let {
                employeeImage.setCircleGradientColor(
                    "#FFFFFF",
                    employee.backgroundColor,
                )
                itemView.setOnClickListener {
                    employeeOnClickListener.onEmployeeClick(adapterPosition)
                }

            }
        }
    }

    interface EmployeeOnClickListener {
        fun onEmployeeClick(position: Int)
    }
}