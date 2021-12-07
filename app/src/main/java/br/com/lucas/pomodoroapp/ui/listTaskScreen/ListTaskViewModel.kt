package br.com.lucas.pomodoroapp.ui.listTaskScreen

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.lucas.pomodoroapp.BuildConfig
import br.com.lucas.pomodoroapp.core.extensions.toAdapterItem
import br.com.lucas.pomodoroapp.core.extensions.toTaskItem
import br.com.lucas.pomodoroapp.database.DataBaseConnect
import br.com.lucas.pomodoroapp.database.Task
import kotlinx.coroutines.launch

class ListTaskViewModel(private val context: Application) : AndroidViewModel(context) {

    val taskList = MutableLiveData<List<ListTaskAdapterItem>>()
    val selectionMode = MutableLiveData<Boolean>(false)
    private val tasksSelected = ArrayList<Task>()

    var previousSelection: ArrayList<Int>? = null
        private set

    fun convertTaskAdapterItemToTask(taskAdapterItem: ListTaskAdapterItem): Task{
        return taskAdapterItem.toTaskItem()
    }

    fun syncSelection(taskAdapterItem: ListTaskAdapterItem, isSelected: Boolean) {
        if (isSelected) {
            val exists = tasksSelected.any { it.uid == taskAdapterItem.uid }
            if (!exists) {
                tasksSelected.add(taskAdapterItem.toTaskItem())
            }
        } else {
            tasksSelected.remove(taskAdapterItem.toTaskItem())
        }

        if (selectionMode.value != isSelectedModeEnabled()) {
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
            val updatedList: List<ListTaskAdapterItem> = tasks.map {
                val task = it.toAdapterItem()
                if (previousSelection?.contains(it.uid) == true) {
                    val taskAdapterItem = task.apply {
                        toggleTask()
                    }
                    syncSelection(taskAdapterItem, taskAdapterItem.isTaskSelected())
                }
                task
            }
            val updatedListWithTimerSwitchState = if(previousSelection?.isNotEmpty() == true){
                updatedList.map {
                    it.copy(isTimerSwitchViewVisible = false)
                }
            } else {
                updatedList
            }
            taskList.postValue(updatedListWithTimerSwitchState)
        }
    }

    fun processPreviousSelection(preSelectedElements: ArrayList<Int>) {
        previousSelection = preSelectedElements
    }
}