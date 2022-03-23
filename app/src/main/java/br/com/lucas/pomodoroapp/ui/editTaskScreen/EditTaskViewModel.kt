package br.com.lucas.pomodoroapp.ui.editTaskScreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucas.pomodoroapp.database.model.Task
import br.com.lucas.pomodoroapp.database.TaskRepository
import br.com.lucas.pomodoroapp.database.model.PomodoroDurations
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

    val isTaskEnabled: Boolean
        get() = task?.uid == preferencesHelper.taskTimerEnabled

    val isTaskNameValid = MutableLiveData<Boolean>()

    var isEditMode = false
        private set

    var task: Task? = null
        private set

    fun setup(task: Task) {
        this.task = task
        this.isEditMode = true
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

    fun onSaveEvent(
        taskName: String,
        pomodoroDurations: PomodoroDurations,
        closeScreen: EditTaskCallback = {},
        toastOfSuccessUpdate: EditTaskCallback = {},
        toastOfSuccessAdd: EditTaskCallback = {},
        toastOfFail: EditTaskCallback = {},
    ) {
        if (this.task == null) {
            saveNewTask(taskName, pomodoroDurations, toastOfSuccessAdd, toastOfFail, closeScreen)
        } else {
            saveSameTask(taskName, pomodoroDurations, closeScreen, toastOfSuccessUpdate, toastOfFail)
        }
    }

    private fun saveSameTask(
        taskName: String,
        pomodoroDurations: PomodoroDurations,
        closeScreen: EditTaskCallback,
        toastOfSuccessUpdate: EditTaskCallback,
        toastOfFail: EditTaskCallback,
    ) {
        checkTaskNameIsValid(task!!.taskName)
        if (isTaskNameValid.value == true) {
            this.task = task!!.copy(taskName = taskName, pomodoroDurations = pomodoroDurations)
            viewModelScope.launch {
                repository.updateTask(task!!)
                toastOfSuccessUpdate()
                closeScreen()
            }
        } else {
            toastOfFail()
        }
    }

    private fun saveNewTask(
        taskName: String,
        pomodoroDurations: PomodoroDurations,
        toastOfSuccessAdd: EditTaskCallback,
        toastOfFail: EditTaskCallback,
        closeScreen: EditTaskCallback,
    ) {
        if (isTaskNameValid.value == true) {
            viewModelScope.launch {
                repository.insertTask(
                    Task(
                        uid = 0,
                        taskName = taskName,
                        pomodoroDurations = pomodoroDurations
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