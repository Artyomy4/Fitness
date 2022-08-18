package ru.teempton.fitnessappcurse.fragments

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import pl.droidsonroids.gif.GifDrawable
import ru.teempton.fitnessappcurse.R
import ru.teempton.fitnessappcurse.adapters.ExerciseModel
import ru.teempton.fitnessappcurse.databinding.ExerciseBinding
import ru.teempton.fitnessappcurse.utils.FragmentManager
import ru.teempton.fitnessappcurse.utils.MainViewModel
import ru.teempton.fitnessappcurse.utils.TimeUtils

class ExerciseFragment : Fragment() {
    private var timer: CountDownTimer? = null
    private lateinit var binding: ExerciseBinding
    private var exerciseCounter = 0
    private var ab: ActionBar? = null
    private var currentDay = 0
    private var exList: ArrayList<ExerciseModel>? = null
    private val model: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentDay = model.currentDay
        exerciseCounter = model.getExerciseCounter()
        ab = (activity as AppCompatActivity).supportActionBar
        model.mutableListExercise.observe(viewLifecycleOwner) {
            exList = it
            nextExercise()
        }
        binding.bNext.setOnClickListener {
            nextExercise()
        }
    }

    private fun nextExercise() {
        if (exerciseCounter > exList?.size!! - 1) {
            exerciseCounter++
            FragmentManager.setFragment(
                DayFinishFragment.newInstance(),
                activity as AppCompatActivity
            )
        } else {
            val ex = exList?.get(exerciseCounter++) ?: return
            showExercise(ex)
            setExerciseType(ex)
            showNextExercise()
        }
    }

    private fun showNextExercise() = with(binding) {
        if (exerciseCounter > exList?.size!! - 1) {
            imNext.setImageDrawable(GifDrawable(root.context.assets, "SN20.gif"))
            tvNextName.text = getString(R.string.done)
        } else {
            val ex = exList?.get(exerciseCounter) ?: return
            setTimeType(ex)
            imNext.setImageDrawable(GifDrawable(root.context.assets, ex.image))
        }
    }

    private fun setTimeType(ex: ExerciseModel) {
        if (ex.time.startsWith("x")) {
            binding.tvNextName.text = ex.name + " " + ex.time
        } else {
            val name = ex.name + ": ${TimeUtils.getTime(ex.time.toLong() * 1000)}"
            binding.tvNextName.text = name
        }
    }

    private fun showExercise(exercise: ExerciseModel) = with(binding) {
        imMain.setImageDrawable(GifDrawable(root.context.assets, exercise.image))
        tvName.text = exercise.name
        val title = "${exerciseCounter} / ${exList?.size}"
        ab?.title = title
    }

    private fun setExerciseType(exercise: ExerciseModel) = with(binding) {
        if (exercise.time.startsWith("x")) {
            binding.progressBar.visibility = View.GONE
            timer?.cancel()
            tvTime.text = exercise.time
        } else {
            binding.progressBar.visibility = View.VISIBLE
            startTimer(exercise)
        }
    }

    private fun startTimer(exercise: ExerciseModel) = with(binding) {
        progressBar.max = exercise.time.toInt() * 1000
        timer?.cancel()
        timer = object : CountDownTimer(exercise.time.toLong() * 1000, 10) {
            override fun onTick(restTime: Long) {
                tvTime.text = TimeUtils.getTime(restTime)
                progressBar.progress = restTime.toInt()
            }

            override fun onFinish() {
                nextExercise()
            }
        }.start()
    }

    override fun onDetach() {
        super.onDetach()
        model.savePref(currentDay.toString(), exerciseCounter - 1)
        timer?.cancel()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ExerciseFragment()
    }
}