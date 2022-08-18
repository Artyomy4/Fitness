package ru.teempton.fitnessappcurse.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.teempton.fitnessappcurse.R
import ru.teempton.fitnessappcurse.adapters.DayModel
import ru.teempton.fitnessappcurse.adapters.DaysAdapter
import ru.teempton.fitnessappcurse.adapters.ExerciseModel
import ru.teempton.fitnessappcurse.databinding.FragmentDaysBinding
import ru.teempton.fitnessappcurse.utils.DialogManager
import ru.teempton.fitnessappcurse.utils.FragmentManager
import ru.teempton.fitnessappcurse.utils.MainViewModel

class DaysFragment : Fragment(), DaysAdapter.Listener {
    private lateinit var adapter: DaysAdapter
    private lateinit var binding: FragmentDaysBinding
    private var ab: ActionBar? = null
    private val model: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.reset) {
            DialogManager.showDialog(
                activity as AppCompatActivity,
                R.string.rest_days_message,
                object :DialogManager.Listener{
                    override fun onClick() {
                        model.pref?.edit()?.clear()?.apply()
                        adapter.submitList(fillDaysArray())
                    }
                }
            )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.currentDay = 0
        initRcView()
        ab = (activity as AppCompatActivity).supportActionBar
        ab?.title = getString(R.string.days)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater.inflate(R.menu.main_menu, menu)
    }

    private fun initRcView() = with(binding) {
        adapter = DaysAdapter(this@DaysFragment)
        rcViewDays.layoutManager = LinearLayoutManager(activity as AppCompatActivity)
        rcViewDays.adapter = adapter
        adapter.submitList(fillDaysArray())
    }

    private fun fillDaysArray(): ArrayList<DayModel> {
        val tArray = ArrayList<DayModel>()
        var daysDoneConter = 0
        resources.getStringArray(R.array.day_exercise).forEach {
            model.currentDay++
            val exCount = it.split(",").size
            tArray.add(DayModel(it, 0, model.getExerciseCounter() == exCount))
        }
        tArray.forEach {
            if (it.isDone) daysDoneConter++
        }
        binding.pB.max = tArray.size
        binding.pB.progress = daysDoneConter
        updateRestDays(tArray.size - daysDoneConter)
        return tArray
    }

    private fun updateRestDays(restDays: Int) = with(binding) {
        val restDaysTemplate = getString(R.string.rest_days)
        tvRestDays.text = String.format(restDaysTemplate, restDays)

    }

    private fun fillExerciseList(day: DayModel) {
        val tempList = ArrayList<ExerciseModel>()
        day.exercises.split(",").forEach {
            val exerciseList = resources.getStringArray(R.array.exercise)
            val exercise = exerciseList[it.toInt()]
            val exerciseArray = exercise.split("|")
            tempList.add(ExerciseModel(exerciseArray[0], exerciseArray[1], false, exerciseArray[2]))
        }
        model.mutableListExercise.value = tempList
    }

    companion object {
        @JvmStatic
        fun newInstance() = DaysFragment()
    }

    override fun onClick(day: DayModel) {
        if (!day.isDone) {
            fillExerciseList(day)
            model.currentDay = day.dayNumber
            FragmentManager.setFragment(
                ExerciseListFragment.newInstance(),
                activity as AppCompatActivity
            )
        }else{
            DialogManager.showDialog(
                activity as AppCompatActivity,
                R.string.rest_day_message,
                object :DialogManager.Listener{
                    override fun onClick() {
                        model.savePref(day.dayNumber.toString(),0)
                        fillExerciseList(day)
                        model.currentDay = day.dayNumber
                        FragmentManager.setFragment(
                            ExerciseListFragment.newInstance(),
                            activity as AppCompatActivity
                        )
                    }
                }
            )
        }
    }
}