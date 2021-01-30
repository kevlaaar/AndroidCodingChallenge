package co.teltech.base.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import co.teltech.base.R
import co.teltech.base.databinding.FragmentDetailsBinding
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
        binding.employeeBiography.text = employeeObject.description
        binding.employeeName.text = employeeObject.name
        Glide.with(requireContext())
            .load(employeeObject.getImageMainUrl())
            .into(binding.employeeImageMotion)
        return binding.root
    }
}