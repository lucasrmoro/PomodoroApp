package br.com.lucas.pomodoroapp.ui.listTaskScreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucas.pomodoroapp.BuildConfig
import br.com.lucas.pomodoroapp.database.Task
import br.com.lucas.pomodoroapp.database.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListTaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    val taskList = MutableLiveData<List<Task>>()
    val selectionMode = MutableLiveData<Boolean>(false)
    private val tasksSelected = ArrayList<Task>()

    var previousSelection: ArrayList<Int> = arrayListOf()
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
                repository.insertTask(
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

    fun deleteTasks() {
        viewModelScope.launch {
            tasksSelected.forEach {
                repository.deleteTask(it)
            }
            selectionMode.value = false
            previousSelection.clear()
            tasksSelected.clear()
            refresh()
        }
    }

    fun getQuantityOfSelectedTasks(): Int {
        return tasksSelected.size
    }

    fun refresh() {
        viewModelScope.launch {
            repository.getAllTasks().collect { listOfTasks ->
                taskList.value = listOfTasks
            }
            selectionMode.postValue(false)
            val updatedList = taskList.value?.map { task ->
                if (previousSelection.contains(task.uid)) {
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