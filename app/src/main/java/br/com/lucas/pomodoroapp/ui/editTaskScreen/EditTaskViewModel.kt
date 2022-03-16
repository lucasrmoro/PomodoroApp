package br.com.lucas.pomodoroapp.ui.editTaskScreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucas.pomodoroapp.database.Task
import br.com.lucas.pomodoroapp.database.TaskRepository
import br.com.lucas.pomodoroapp.helpers.PreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias EditTaskCallback = () -> Unit

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val preferencesHelper: PreferencesHelper,
) : ViewModel() {

    var total: Int = 25
        private set

    val isTaskEnabled: Boolean
        get() = task?.uid == preferencesHelper.taskTimerEnabled

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
    }

    fun delete(closeScreen: EditTaskCallback = {}, toastOfSuccess: EditTaskCallback = {}) {
        val task = task ?: return
        viewModelScope.launch {
            repository.deleteTask(task)
            toastOfSuccess()
            closeScreen()
        }
    }

    fun checkTaskNameIsValid(taskName: String) {
        isTaskNameValid.value = taskName.length >= 3
    }

    fun checkTaskTimeIsValid(hour: Int, minute: Int) {
        val hoursInMinutes = hour * HOUR_ON_MINUTES
        total = hoursInMinutes + minute
        checkTotalTime()
    }

    private fun checkTotalTime() {
        isPomodoroTimerValid.value = total in 25..60
    }

    fun onSaveEvent(
        taskName: String,
        closeScreen: EditTaskCallback = {},
        toastOfSuccessUpdate: EditTaskCallback = {},
        toastOfSuccessAdd: EditTaskCallback = {},
        toastOfFail: EditTaskCallback = {}
    ) {
        if (task == null) {
            saveNewTask(taskName, toastOfSuccessAdd, toastOfFail, closeScreen)
        } else {
            task!!.taskName = taskName
            task!!.taskMinutes = total
            checkTaskNameIsValid(task!!.taskName)
            checkTotalTime()
            saveSameTask(task!!, closeScreen, toastOfSuccessUpdate, toastOfFail)
        }
    }

    private fun saveSameTask(
        task: Task,
        closeScreen: EditTaskCallback,
        toastOfSuccessUpdate: EditTaskCallback,
        toastOfFail: EditTaskCallback
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
        toastOfSuccessAdd: EditTaskCallback,
        toastOfFail: EditTaskCallback,
        closeScreen: EditTaskCallback
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