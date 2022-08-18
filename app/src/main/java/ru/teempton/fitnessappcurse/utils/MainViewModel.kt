package ru.teempton.fitnessappcurse.utils

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.teempton.fitnessappcurse.adapters.ExerciseModel

class MainViewModel:ViewModel() {
    val mutableListExercise = MutableLiveData<ArrayList<ExerciseModel>>()
    var pref:SharedPreferences? = null
    var currentDay = 0

    fun savePref(key:String, value:Int){
        pref?.edit()?.putInt(key, value)?.apply()
    }

    fun getExerciseCounter():Int{
        return pref?.getInt(currentDay.toString(),0) ?: 0
    }
}