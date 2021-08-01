package br.com.lucas.pomodoroapp.ui

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucas.pomodoroapp.database.DataBaseConnect
import br.com.lucas.pomodoroapp.database.Task
import kotlinx.coroutines.launch

class EditTaskViewModel : ViewModel() {

    var total: Int = 25
        private set

    val isPomodoroTimerValid = MutableLiveData<Boolean>()
    val isTaskNameValid = MutableLiveData<Boolean>()
    private val HOUR_ON_MINUTES = 60

    var isEditMode = false
        private set

    var task: Task? = null
        private set

    fun setup(task: Task) {
        this.task = task
        this.isEditMode = true
    }

    fun delete(context: Context, closeScreen: () -> Unit){
        val task = task ?: return
            viewModelScope.launch {
                DataBaseConnect.getTaskDao(context).deleteTask(task)
                closeScreen()
            }
    }

    fun validTask(content: String) {
        isTaskNameValid.value = content.length >= 3
    }

    fun checkTimeIsValid(hour: Int, minute: Int) {
        val hoursInMinutes = hour * HOUR_ON_MINUTES
        total = hoursInMinutes + minute
        checkTotalTime()
    }

    private fun checkTotalTime() {
        isPomodoroTimerValid.value = total in 25..60
    }

    fun onSaveEvent(context: Context, taskName: String, closeScreen: (() -> Unit)) {
        if (task == null) {
            saveNewTask(context, taskName, closeScreen)
        } else {
            task!!.taskName = taskName
            task!!.taskMinutes = total
            validTask(task!!.taskName)
            checkTotalTime()
            saveSameTask(context, task!!, closeScreen)
        }
    }

    private fun saveSameTask(
        context: Context,
        task: Task,
        closeScreen: () -> Unit
    ) {
        if (isPomodoroTimerValid.value == true && isTaskNameValid.value == true) {
            viewModelScope.launch {
                DataBaseConnect.getTaskDao(context).updateTask(
                    task
                )
                Toast.makeText(context, "Successfully changed!", Toast.LENGTH_SHORT).show()
                closeScreen()
            }
        } else {
            Toast.makeText(context, "Fill all required fields!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveNewTask(
        context: Context,
        taskName: String,
        closeScreen: () -> Unit
    ) {
        if (isPomodoroTimerValid.value == true && isTaskNameValid.value == true) {
            viewModelScope.launch {
                DataBaseConnect.getTaskDao(context).insertTask(
                    Task(
                        taskName = taskName,
                        taskMinutes = total,
                        uid = 0
                    )
                )
                Toast.makeText(context, "Successfully saved!", Toast.LENGTH_SHORT).show()
                closeScreen()
            }
        } else {
            Toast.makeText(context, "Fill all required fields!", Toast.LENGTH_SHORT).show()
        }
    }

}