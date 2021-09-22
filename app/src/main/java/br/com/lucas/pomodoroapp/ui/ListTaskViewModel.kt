package br.com.lucas.pomodoroapp.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.lucas.pomodoroapp.R
import br.com.lucas.pomodoroapp.database.DataBaseConnect
import br.com.lucas.pomodoroapp.database.Task
import kotlinx.coroutines.launch

class ListTaskViewModel(private val context: Application) : AndroidViewModel(context) {

    val taskList = MutableLiveData<List<Task>>()
    val selectionMode = MutableLiveData<Boolean>(false)
    private val tasksSelected = ArrayList<Task>()

    fun syncSelection(task: Task, isSelected: Boolean) {
        if (isSelected) {
            val exists = tasksSelected.any { it.taskName == task.taskName }
            if (!exists) {
                tasksSelected.add(task)
            }
        } else {
            tasksSelected.remove(task)
        }

        if (isSelectedModeEnabled() != selectionMode.value) {
            selectionMode.value = isSelectedModeEnabled()
        }
    }

    fun isSelectedModeEnabled(): Boolean {
        return tasksSelected.isNotEmpty()
    }

    fun deleteTasks(context: Context, toast: () -> Unit) {
        viewModelScope.launch {
            tasksSelected.forEach {
                DataBaseConnect.getTaskDao(context).deleteTask(it)
            }
            selectionMode.postValue(false)
            tasksSelected.clear()
            refresh()
        }
    }

    fun setupConfirmationDialogMessage(context: Context): String {
        return if (isSelectedTasksAmountAreMoreThanOne()) {
            "Are you sure you want to delete ${getQuantityOfSelectedTasks()} tasks?"
        } else {
            context.getString(R.string.delete_confirmation_message)
        }
    }

    private fun isSelectedTasksAmountAreMoreThanOne(): Boolean {
        return getQuantityOfSelectedTasks() > 1
    }

    private fun getQuantityOfSelectedTasks(): Int {
        return tasksSelected.size
    }

    fun refresh() {
        viewModelScope.launch {
            taskList.postValue(
                DataBaseConnect.getTaskDao(context).getAll()
            )
        }
    }
}