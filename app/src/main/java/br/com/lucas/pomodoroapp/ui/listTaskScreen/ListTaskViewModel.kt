package br.com.lucas.pomodoroapp.ui.listTaskScreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucas.pomodoroapp.core.extensions.toAdapterItems
import br.com.lucas.pomodoroapp.core.extensions.toTaskItem
import br.com.lucas.pomodoroapp.database.Task
import br.com.lucas.pomodoroapp.database.TaskRepository
import br.com.lucas.pomodoroapp.mediators.AlarmMediator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListTaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val alarmMediator: AlarmMediator,
    private val listTaskViewStateManager: ListTaskViewStateManager
) : ViewModel() {

    val taskList = MutableLiveData<List<AdapterItem>>()
    val selectionMode = MutableLiveData(false)
    private val tasksSelected = ArrayList<Task>()
    private var taskTimerEnabled = -1

    fun convertAdapterItemToTask(adapterItem: AdapterItem): Task {
        return adapterItem.toTaskItem()
    }

    fun syncSelection(adapterItem: AdapterItem) {
        viewModelScope.launch {
            val task = adapterItem.toTaskItem()
            val newSelectionState =
                listTaskViewStateManager.toggleTaskSelection(adapterItem).selectionState

            if (newSelectionState == SelectionState.SELECTED) {
                val notExists = tasksSelected.none { it.uid == adapterItem.uid }
                if (notExists) {
                    tasksSelected.add(task)
                }
            } else {
                tasksSelected.remove(task)
            }

            refreshStateOfTasks()

            if (isSelectionModeEnabled() != selectionMode.value) {
                selectionMode.value = isSelectionModeEnabled()
            }
        }
    }

    fun syncTaskTimer(task: AdapterItem, isTimerEnabled: Boolean) {
        viewModelScope.launch {
            alarmMediator.syncTaskTimer(isTimerEnabled, task.uid, task.taskMinutes)
            refreshStateOfTasks()
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

    fun isSelectionModeEnabled(): Boolean {
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

    fun refreshStateOfTasks(listOfTasks: List<AdapterItem>? = taskList.value) {
        taskTimerEnabled = alarmMediator.taskTimerEnabled
        listTaskViewStateManager.sync(
            taskList = listOfTasks,
            isSelectionModeEnabled = isSelectionModeEnabled(),
            tasksSelected = tasksSelected,
            taskTimerEnabled = taskTimerEnabled)
        taskList.postValue(listTaskViewStateManager.getTaskListUpdated())
    }

    fun refresh() {
        viewModelScope.launch {
            repository.getAllTasks().collect { listOfTasks ->
                refreshStateOfTasks(listOfTasks.toAdapterItems())
            }
        }
    }
}