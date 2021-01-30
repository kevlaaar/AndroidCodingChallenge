package co.teltech.base.ui.main

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import co.teltech.base.R
import co.teltech.base.databinding.FragmentMainBinding
import co.teltech.base.shared.kotlin.changeBackgroundAnimated
import co.teltech.base.shared.util.GlideDrawableCrossFade
import co.teltech.base.ui.main.adapters.EmployeeAdapter
import co.teltech.base.ui.main.adapters.TeamsAdapter
import co.teltech.base.vo.Employee
import co.teltech.base.vo.Team
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class MainFragment : Fragment(), EmployeeAdapter.EmployeeOnClickListener {
    private val viewModel: MainFragmentViewModel by viewModel()
    private lateinit var binding: FragmentMainBinding
    private var employeeList: List<Employee> = ArrayList()
    private lateinit var employeeAdapter: EmployeeAdapter
    private lateinit var teamsAdapter: TeamsAdapter
    val snapHelper = PagerSnapHelper()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.employeeRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        employeeAdapter = EmployeeAdapter(requireContext(), employeeList, this)
        val itemAnimator: DefaultItemAnimator = object : DefaultItemAnimator() {
            override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
                return true
            }
        }
        binding.employeeRecycler.itemAnimator = itemAnimator
        binding.employeeRecycler.adapter = employeeAdapter
        viewModel.teamList.observe(viewLifecycleOwner, {
            it?.let { refreshTeams(it) } })
        viewModel.employeeList.observe(viewLifecycleOwner, {
            it?.let {
                employeeList = it
                refreshEmployees(it)
            }
        })
        addSnapperListener()
    }
    private fun addSnapperListener(){
        binding.toggleButtonLayout.setTransitionListener(object: MotionLayout.TransitionListener{
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                if(p1 == R.id.toggleOff){
                    snapHelper.attachToRecyclerView(binding.employeeRecycler)
                } else if(p1 == R.id.toggleOn){
                    snapHelper.attachToRecyclerView(null)
                }
            }
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        })
    }
    fun navigateToDetails(employeeObject: Employee) {
        val bundle = bundleOf("employeeObject" to employeeObject)
        findNavController().navigate(R.id.action_mainFragment_to_employeeDetailsFragment, bundle)
    }
    private fun refreshEmployees(employeeList: List<Employee>?) {
        Timber.e("REFRESUJEM RECYCLER")
        employeeList?.let {
            employeeAdapter.setData(it)
        }
    }
    private fun refreshTeams(teamList: List<Team>){
        Timber.e("REFRESUJEM TEAMS")
        teamsAdapter = TeamsAdapter(requireContext(), teamList)
        binding.teamInfoRecycler.adapter = teamsAdapter
    }

    override fun onEmployeeClick(position: Int) {
        val employee = employeeList[position]
        Glide.with(requireContext())
            .load(employee.getImageMainUrl())
            .circleCrop()
            .transition(DrawableTransitionOptions.with(GlideDrawableCrossFade()))
            .into(binding.mainImage)
        binding.mainImage.changeBackgroundAnimated(
            "#FFFFFF",
            employee.backgroundColor,
            360f,
            GradientDrawable.Orientation.LEFT_RIGHT,
            GradientDrawable.LINEAR_GRADIENT
        )
        binding.mainImage.setOnClickListener{ navigateToDetails(employee) }
        binding.backgroundCircleImage.changeBackgroundAnimated(
            employee.backgroundColor,
            "#FFFFFF",
            480f,
            GradientDrawable.Orientation.LEFT_RIGHT,
            GradientDrawable.RADIAL_GRADIENT
        )
    }
}

