package br.com.lucas.pomodoroapp.ui.listTaskScreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucas.pomodoroapp.BuildConfig
import br.com.lucas.pomodoroapp.core.extensions.toAdapterItems
import br.com.lucas.pomodoroapp.core.extensions.toTaskItem
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

    val taskList = MutableLiveData<List<AdapterItem>>()
    val selectionMode = MutableLiveData<Boolean>(false)
    private val tasksSelected = ArrayList<Task>()

    fun convertAdapterItemToTask(adapterItem: AdapterItem): Task {
        return adapterItem.toTaskItem()
    }

    fun syncSelection(taskAdapterItem: AdapterItem) {
        val task = taskAdapterItem.toTaskItem()
        if (taskAdapterItem.isTaskSelected) {
            val notExists = tasksSelected.none { it.uid == taskAdapterItem.uid }
            if (notExists) {
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
            tasksSelected.clear()
            refresh()
        }
    }

    fun getQuantityOfSelectedTasks(): Int {
        return tasksSelected.size
    }

    private fun checkTaskIsSelected(task: AdapterItem){
        if(tasksSelected.any{ it.uid == task.uid}){
            task.toggleTask()
            syncSelection(task)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            repository.getAllTasks().collect { listOfTasks ->
                val updatedList = listOfTasks.toAdapterItems().map { task ->
                    checkTaskIsSelected(task)
                    task
                }
                taskList.postValue(updatedList)
            }
        }
    }
}