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

    private var total: Int = 25

    val isPomodoroTimerValid = MutableLiveData<Boolean>()
    val isTaskNameValid = MutableLiveData<Boolean>()
    private val HOUR_ON_MINUTES = 60

    val onTaskAlreadyExist = MutableLiveData<Task>()

    fun setup(task: Task) {
        onTaskAlreadyExist.value = task
    }

    fun validTask(content: String) {
        isTaskNameValid.value = content.length >= 3
    }

    fun checkTimeIsValid(hour: Int, minute: Int) {
        val hoursInMinutes = hour * HOUR_ON_MINUTES
        total = hoursInMinutes + minute
        isPomodoroTimerValid.value = total in 25..60
    }

    fun onSaveEvent(context: Context, taskName: String, closeScreen : (()->Unit)) {
        // TODO - [EditTaskSupport] 6. Check if the task already exists you should update the data base, else you keep the same code to create a new one
        saveNewTask(context, taskName, closeScreen)
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