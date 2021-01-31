package co.teltech.base.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import co.teltech.base.R
import co.teltech.base.databinding.FragmentDetailsBinding
import co.teltech.base.shared.kotlin.setBackgroundColorWithoutChangingShape
import co.teltech.base.vo.Employee
import com.bumptech.glide.Glide

class DetailsFragment: Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    private lateinit var employeeObject: Employee
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        employeeObject = arguments?.getSerializable("employeeObject") as Employee
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false)
        binding.backButtonImage.setBackgroundColorWithoutChangingShape(employeeObject.backgroundColor, 255)
        binding.employeeTitleLayout.setBackgroundColorWithoutChangingShape(employeeObject.backgroundColor, 255)
        binding.departmentLabel.setBackgroundColorWithoutChangingShape(employeeObject.backgroundColor, 255)
        binding.descriptionLabel.setBackgroundColorWithoutChangingShape(employeeObject.backgroundColor, 255)
        binding.employeeDescription.text = employeeObject.description
        binding.employeeDepartment.text = employeeObject.getDepartmentCapitalised()
        val employeeFullName = if(employeeObject.surname.isNotEmpty()) {
            "${employeeObject.name} ${employeeObject.surname}"
        } else {
            employeeObject.name
        }
        binding.employeeName.text = employeeFullName
        Glide.with(requireContext())
            .load(employeeObject.getImageMainUrl())
            .into(binding.employeeImageMotion)
        binding.backButtonImage.setOnClickListener { findNavController().popBackStack() }
        return binding.root
    }
}