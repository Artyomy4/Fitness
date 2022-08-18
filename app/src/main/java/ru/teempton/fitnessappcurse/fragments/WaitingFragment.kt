package ru.teempton.fitnessappcurse.fragments

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import ru.teempton.fitnessappcurse.R
import ru.teempton.fitnessappcurse.databinding.WaitingFragmentBinding
import ru.teempton.fitnessappcurse.utils.FragmentManager
import ru.teempton.fitnessappcurse.utils.TimeUtils

const val COUNTDOWN_TIME = 11000L  //ctrl+shift+u L - тип Лонг
class WaitingFragment : Fragment() {
    private lateinit var binding: WaitingFragmentBinding
    private lateinit var timer:CountDownTimer
    private var ab: ActionBar? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = WaitingFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pBar.max = COUNTDOWN_TIME.toInt()
        startTimer()
        ab = (activity as AppCompatActivity).supportActionBar
        ab?.title = getString(R.string.waiting)
    }

    private fun startTimer() = with(binding){
        timer = object :CountDownTimer(COUNTDOWN_TIME,10){
            override fun onTick(restTime: Long) {
                tvTimer.text = TimeUtils.getTime(restTime)
                pBar.progress = restTime.toInt()
            }

            override fun onFinish() {
                FragmentManager.setFragment(ExerciseFragment.newInstance(),activity as AppCompatActivity)
            }

        }.start()
    }

    override fun onDetach() {
        super.onDetach()
        timer.cancel()
    }

    companion object {
        @JvmStatic
        fun newInstance() = WaitingFragment()
    }
}