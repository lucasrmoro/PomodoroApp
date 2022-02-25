package br.com.lucas.pomodoroapp.ui.editTaskScreen

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucas.pomodoroapp.core.receiver.AlarmReceiver
import br.com.lucas.pomodoroapp.database.Task
import br.com.lucas.pomodoroapp.database.TaskRepository
import br.com.lucas.pomodoroapp.helpers.AlarmManagerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val alarmManagerHelper: AlarmManagerHelper
) : ViewModel() {

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
        total = task.taskMinutes

        Log.d(AlarmReceiver.TAG, "Starting ${task.taskName}")
        Log.d(AlarmReceiver.TAG,
            "task time: ${task.taskMinutes}")

        alarmManagerHelper.setExactAlarm(task.taskMinutes)
    }

    fun delete(closeScreen: () -> Unit, toastOfSuccess: () -> Unit) {
        val task = task ?: return
        viewModelScope.launch {
            repository.deleteTask(task)
            toastOfSuccess()
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

    fun onSaveEvent(
        taskName: String,
        closeScreen: (() -> Unit) = {},
        toastOfSuccessUpdate: () -> Unit= {},
        toastOfSuccessAdd: () -> Unit = {},
        toastOfFail: () -> Unit = {}
    ) {
        if (task == null) {
            saveNewTask(taskName, toastOfSuccessAdd, toastOfFail, closeScreen)
        } else {
            task!!.taskName = taskName
            task!!.taskMinutes = total
            validTask(task!!.taskName)
            checkTotalTime()
            saveSameTask(task!!, closeScreen, toastOfSuccessUpdate, toastOfFail)
        }
    }

    private fun saveSameTask(
        task: Task,
        closeScreen: () -> Unit,
        toastOfSuccessUpdate: () -> Unit,
        toastOfFail: () -> Unit
    ) {
        if (isPomodoroTimerValid.value == true && isTaskNameValid.value == true) {
            viewModelScope.launch {
                repository.updateTask(task)
                toastOfSuccessUpdate()
                closeScreen()
            }
        } else {
            toastOfFail()
        }
    }

    private fun saveNewTask(
        taskName: String,
        toastOfSuccessAdd: () -> Unit,
        toastOfFail: () -> Unit,
        closeScreen: () -> Unit
    ) {
        if (isPomodoroTimerValid.value == true && isTaskNameValid.value == true) {
            viewModelScope.launch {
                repository.insertTask(
                    Task(
                        taskName = taskName,
                        taskMinutes = total,
                        uid = 0
                    )
                )
                toastOfSuccessAdd()
                closeScreen()
            }
        } else {
            toastOfFail()
        }
    }
}