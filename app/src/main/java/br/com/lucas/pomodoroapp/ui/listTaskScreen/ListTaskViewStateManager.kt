package br.com.lucas.pomodoroapp.ui.listTaskScreen

import br.com.lucas.pomodoroapp.database.model.Task
import br.com.lucas.pomodoroapp.ui.listTaskScreen.SelectionState.*
import br.com.lucas.pomodoroapp.ui.listTaskScreen.SwitchState.*

class ListTaskViewStateManager {

    private var taskList: List<AdapterItem>? = null
    private var tasksSelected: List<Task>? = null
    private var taskWithPomodoroTimerEnabled: Task? = null
    private var isSelectionModeEnabled: Boolean = true

    fun sync(
        taskList: List<AdapterItem>?,
        isSelectionModeEnabled: Boolean,
        tasksSelected: List<Task>,
        taskWithPomodoroTimerEnabled: Task?,
    ) {
        this.taskList = taskList
        this.isSelectionModeEnabled = isSelectionModeEnabled
        this.tasksSelected = tasksSelected
        this.taskWithPomodoroTimerEnabled = taskWithPomodoroTimerEnabled
    }

    fun toggleTaskSelection(task: AdapterItem): AdapterItem = task.toggleSelectionState()

    fun getTaskListUpdated(): List<AdapterItem>? {
        var updatedList = taskList
        updatedList = updatedList?.map {
            it.copy(selectionState = updateTaskSelection(it),
                isSwitchVisible = updateSwitchVisibility(it),
                switchState = updateSwitchState(it))
        }
        return updatedList
    }

    private fun updateTaskSelection(task: AdapterItem): SelectionState =
        if (tasksSelected?.any { it.uid == task.uid } == true) SELECTED else NOT_SELECTED


    private fun updateSwitchVisibility(task: AdapterItem) =
        if (isSelectionModeEnabled) task.uid == taskWithPomodoroTimerEnabled?.uid else true

    private fun updateSwitchState(task: AdapterItem): SwitchState =
        taskWithPomodoroTimerEnabled?.run {
            if(task.uid == taskWithPomodoroTimerEnabled?.uid) ENABLED else DISABLED
        } ?: DEFAULT
}