package br.com.lucas.pomodoroapp.ui.listTaskScreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucas.pomodoroapp.core.extensions.toAdapterItems
import br.com.lucas.pomodoroapp.core.extensions.toTaskItem
import br.com.lucas.pomodoroapp.database.TaskRepository
import br.com.lucas.pomodoroapp.database.model.PomodoroDurations
import br.com.lucas.pomodoroapp.database.model.Task
import br.com.lucas.pomodoroapp.helpers.PomodoroTimerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ListTaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val listTaskViewStateManager: ListTaskViewStateManager,
    private val pomodoroTimerHelper: PomodoroTimerHelper
) : ViewModel() {

    val taskList = MutableLiveData<List<AdapterItem>>()
    val selectionMode = MutableLiveData(false)
    private val tasksSelected = ArrayList<Task>()
    private var taskWithPomodoroTimerEnabled: Task? = null

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

    fun syncPomodoroCountdown(task: AdapterItem, isTimerEnabled: Boolean) {
        viewModelScope.launch {
            pomodoroTimerHelper.isPomodoroTimerEnabled(isTimerEnabled, task.toTaskItem())
            refreshStateOfTasks()
        }
    }

    fun addTenTasksOnDataBase() {
        viewModelScope.launch {
            for (i in 1..10) {
                repository.insertTask(
                    Task(
                        uid = 0,
                        taskName = "Test Task $i",
                        pomodoroDurations = PomodoroDurations(),
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
        taskWithPomodoroTimerEnabled = pomodoroTimerHelper.taskWithPomodoroTimerEnabled
        listTaskViewStateManager.sync(
            taskList = listOfTasks,
            isSelectionModeEnabled = isSelectionModeEnabled(),
            tasksSelected = tasksSelected,
            taskWithPomodoroTimerEnabled = taskWithPomodoroTimerEnabled)
        taskList.postValue(listTaskViewStateManager.getTaskListUpdated())
    }

    fun refresh() {
        viewModelScope.launch {
            repository.getAllTasks().collect { listOfTasks ->
                Timber.d("List of tasks from DB: ${listOfTasks.toAdapterItems()}")
                refreshStateOfTasks(listOfTasks.toAdapterItems())
            }
        }
    }
}