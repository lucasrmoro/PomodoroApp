package br.com.lucas.pomodoroapp.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditTaskViewModel : ViewModel() {

    val isPomodoroTimerValid = MutableLiveData<Boolean>()
    val isTaskNameValid = MutableLiveData<Boolean>()
    private val MINUTES_ON_HOUR = 60

    fun validTask(content: String) {
        isTaskNameValid.value = content.length >= 3
    }

    fun checkTimeIsValid(hour: Int, minute: Int) {
        val hoursInMinutes = hour * MINUTES_ON_HOUR
        val total = hoursInMinutes + minute
        isPomodoroTimerValid.value = total in 25..60
    }

    fun onSaveEvent() {
        if (isPomodoroTimerValid.value == true && isTaskNameValid.value == true) {
            Log.d("SAVE_ACTION", "Data is valid, saving on data base")
        } else {
            Log.d("SAVE_ACTION", "Data is not valid")
        }
    }

}