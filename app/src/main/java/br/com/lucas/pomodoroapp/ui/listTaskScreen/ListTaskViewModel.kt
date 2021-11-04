package br.com.lucas.pomodoroapp.ui.listTaskScreen

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.lucas.pomodoroapp.BuildConfig
import br.com.lucas.pomodoroapp.database.DataBaseConnect
import br.com.lucas.pomodoroapp.database.Task
import kotlinx.coroutines.launch

class ListTaskViewModel(private val context: Application) : AndroidViewModel(context) {

    val taskList = MutableLiveData<List<Task>>()
    val selectionMode = MutableLiveData<Boolean>(false)
    private val tasksSelected = ArrayList<Task>()

    var previousSelection: ArrayList<Int>? = null
        private set

    fun syncSelection(task: Task, isSelected: Boolean) {
        if (isSelected) {
            val exists = tasksSelected.any { it.uid == task.uid }
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

    fun addTenTasksOnDataBase() {
        viewModelScope.launch {
            for (i in 1..10) {
                DataBaseConnect.getTaskDao(context).insertTask(
                    Task(
                        taskName = "Test Task $i",
                        taskMinutes = 25,
                        uid = 0
                    )
                )
            }
            refresh()
        }
    }

    fun isDebugMode(): Boolean {
        return BuildConfig.DEBUG
    }

    fun isSelectedModeEnabled(): Boolean {
        return tasksSelected.isNotEmpty()
    }

    fun deleteTasks(context: Context) {
        viewModelScope.launch {
            tasksSelected.forEach {
                DataBaseConnect.getTaskDao(context).deleteTask(it)
            }
            tasksSelected.clear()
            refresh()
        }
    }

    fun getQuantityOfSelectedTasks(): Int {
        return tasksSelected.size
    }

    fun refresh() {
        viewModelScope.launch {
            selectionMode.postValue(false)
            val tasks = DataBaseConnect.getTaskDao(context).getAll()
            val updatedList = tasks.map { task ->
                if (previousSelection?.contains(task.uid) == true) {
                    task.toggleTask()
                    syncSelection(task, task.isTaskSelected())
                }
                task
            }
            taskList.postValue(updatedList)
        }
    }

    fun processPreviousSelection(preSelectedElements: ArrayList<Int>) {
        previousSelection = preSelectedElements
    }
}