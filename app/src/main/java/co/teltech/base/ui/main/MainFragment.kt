package co.teltech.base.ui.main

import android.content.res.Configuration
import android.graphics.Bitmap
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
import androidx.recyclerview.widget.*
import co.teltech.base.R
import co.teltech.base.databinding.FragmentMainBinding
import co.teltech.base.shared.kotlin.changeBackgroundAnimated
import co.teltech.base.shared.kotlin.setBackgroundColorWithoutChangingShape
import co.teltech.base.shared.util.GlideDrawableCrossFade
import co.teltech.base.ui.main.adapters.EmployeeAdapter
import co.teltech.base.ui.main.adapters.GridEmployeeAdapter
import co.teltech.base.ui.main.adapters.TeamsAdapter
import co.teltech.base.ui.popup.MessageDialogFragment
import co.teltech.base.vo.Employee
import co.teltech.base.vo.GridEmployee
import co.teltech.base.vo.Team
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainFragment : Fragment(), EmployeeAdapter.EmployeeOnClickListener,
    GridEmployeeAdapter.GridEmployeeOnClickListener {
    private val viewModel: MainFragmentViewModel by viewModel()
    private lateinit var binding: FragmentMainBinding
    private var employeeList: List<Employee> = ArrayList()
    private var teamList: List<Team> = ArrayList()
    private lateinit var employeeAdapter: EmployeeAdapter
    private var gridEmployeeList: ArrayList<GridEmployee> = ArrayList()
    private lateinit var gridEmployeeAdapter: GridEmployeeAdapter
    private lateinit var teamsAdapter: TeamsAdapter
    private var screenOrientation = -1
    val snapHelper = PagerSnapHelper()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
//        BaseApp.firebaseAnalytics.logEvent(...
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        screenOrientation = this.resources.configuration.orientation
        if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridEmployeeAdapter = GridEmployeeAdapter(requireContext(), this)
            binding.employeeRecycler.adapter = gridEmployeeAdapter
            val gridLayoutManager = GridLayoutManager(requireContext(), 3)
            gridLayoutManager.spanSizeLookup = (object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (gridEmployeeList[position].viewType == 3) gridLayoutManager.spanCount else 1
                }
            })
            binding.employeeRecycler.layoutManager = gridLayoutManager
            binding.employeeRecycler.clipChildren = false
            GridLayoutManager(requireContext(), 3)
        } else {
            employeeAdapter = EmployeeAdapter(requireContext(), this)
            binding.employeeRecycler.adapter = employeeAdapter
            binding.employeeRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        val itemAnimator: DefaultItemAnimator = object : DefaultItemAnimator() {
            override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
                return true
            }
        }
        binding.employeeRecycler.itemAnimator = itemAnimator
        viewModel.teamList.observe(viewLifecycleOwner, {
            it?.let { refreshTeams(it) }
        })
        addSnapperListener()
        if (viewModel.listState != null) {
            binding.employeeRecycler.layoutManager?.onRestoreInstanceState(viewModel.listState)

            viewModel.selectedEmployeeObject?.let { employee ->
                if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                    loadEmployeeInLandscapeMode(employee)
                } else {
                    loadEmployeeInPortraitMode(employee)
                }
            }

        }
        viewModel.internetConnectionFlag.observe(viewLifecycleOwner, {
            if (it) {
                resetEmployeeObserver()
            } else {
                showMessageDialog(R.string.no_internet)
            }
        })
        binding.settingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
        }

    }

    private fun resetEmployeeObserver() {
        viewModel.employeeList.removeObservers(this)
        viewModel.employeeList.observe(viewLifecycleOwner, {
            it?.let {
                employeeList = it
                gridEmployeeList = viewModel.gridEmployeeList
                refreshEmployees(it)
            }
        })
    }

    private fun addSnapperListener() {
        binding.toggleButtonLayout?.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                if (p1 == R.id.toggleOff) {
                    snapHelper.attachToRecyclerView(binding.employeeRecycler)
                } else if (p1 == R.id.toggleOn) {
                    snapHelper.attachToRecyclerView(null)
                }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        })
    }

    private fun navigateToDetails(employeeObject: Employee) {
        val bundle = bundleOf("employeeObject" to employeeObject)
        findNavController().navigate(
            R.id.action_mainFragment_to_employeeDetailsFragment,
            bundle
        )
    }

    private fun refreshEmployees(employeeList: List<Employee>?) {
        employeeList?.let {
            if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                gridEmployeeAdapter.setData(gridEmployeeList)
            } else {
                employeeAdapter.setData(it)
            }
        }
    }

    private fun refreshTeams(teamList: List<Team>) {
        teamsAdapter = TeamsAdapter(requireContext(), teamList)
        binding.teamInfoRecycler?.adapter = teamsAdapter
    }

    override fun onEmployeeClick(position: Int) {
        val employeeObject = employeeList[position]
        loadEmployeeInPortraitMode(employeeObject)
        viewModel.selectedEmployeeObject = employeeObject
    }

    override fun updateEmployeeBitmap(position: Int, imageBitmap: Bitmap) {
        if(position>0 && position < employeeList.size)
        employeeList[position].imageBitmap = imageBitmap
    }

    override fun onGridEmployeeClick(position: Int) {
        val employeeObject = gridEmployeeList[position].employeeObject as Employee
        loadEmployeeInLandscapeMode(employeeObject)
        viewModel.selectedEmployeeObject = employeeObject
    }

    private fun loadEmployeeInPortraitMode(employeeObject: Employee) {
        Glide.with(requireContext())
            .load(employeeObject.getImageMainUrl())
            .circleCrop()
            .transition(DrawableTransitionOptions.with(GlideDrawableCrossFade()))
            .into(binding.mainImage)
        binding.mainImage.changeBackgroundAnimated(
            "#FFFFFF",
            employeeObject.backgroundColor,
            360f,
            GradientDrawable.Orientation.LEFT_RIGHT,
            GradientDrawable.LINEAR_GRADIENT
        )
        binding.backgroundCircleImage.changeBackgroundAnimated(
            employeeObject.backgroundColor,
            "#FFFFFF",
            600f,
            GradientDrawable.Orientation.LEFT_RIGHT,
            GradientDrawable.RADIAL_GRADIENT
        )
        binding.mainImage.setOnClickListener { navigateToDetails(employeeObject) }
    }

    private fun loadEmployeeInLandscapeMode(employeeObject: Employee) {
        Glide.with(requireContext())
            .load(employeeObject.getImageMainUrl())
            .circleCrop()
            .transition(DrawableTransitionOptions.with(GlideDrawableCrossFade()))
            .into(binding.mainImage)
        binding.mainImage.changeBackgroundAnimated(
            "#FFFFFF",
            employeeObject.backgroundColor,
            360f,
            GradientDrawable.Orientation.LEFT_RIGHT,
            GradientDrawable.LINEAR_GRADIENT
        )
        binding.backgroundCircleImage.changeBackgroundAnimated(
            employeeObject.backgroundColor,
            "#FFFFFF",
            600f,
            GradientDrawable.Orientation.LEFT_RIGHT,
            GradientDrawable.RADIAL_GRADIENT
        )
        binding.departmentLabel?.setBackgroundColorWithoutChangingShape(
            employeeObject.backgroundColor,
            255
        )
        binding.descriptionLabel?.setBackgroundColorWithoutChangingShape(
            employeeObject.backgroundColor,
            255
        )
        binding.employeeDescription?.text = employeeObject.getFullDescription()
        binding.employeeDepartment?.text = employeeObject.getDepartmentCapitalised()
        val employeeFullName = if (employeeObject.surname.isNotEmpty()) {
            "${employeeObject.name} ${employeeObject.surname}"
        } else {
            employeeObject.name
        }
        binding.employeeName?.text = employeeFullName
    }

    private fun showMessageDialog(messageResource: Int) {
        val ft = childFragmentManager.beginTransaction()
        val newFragment = MessageDialogFragment.newInstance(requireContext().getString(messageResource), null, null)
        newFragment.show(ft, "prompt")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.listState = binding.employeeRecycler.layoutManager?.onSaveInstanceState()
    }
}

