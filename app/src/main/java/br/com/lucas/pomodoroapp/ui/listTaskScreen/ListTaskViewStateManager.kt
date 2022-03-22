package br.com.lucas.pomodoroapp.ui.listTaskScreen

import br.com.lucas.pomodoroapp.database.model.Task

class ListTaskViewStateManager {

    private var taskList: List<AdapterItem>? = null
    private var tasksSelected: List<Task>? = null
    private var taskTimerEnabled: Int = -1
    private var isSelectionModeEnabled: Boolean = true

    fun sync(
        taskList: List<AdapterItem>?,
        isSelectionModeEnabled: Boolean,
        tasksSelected: List<Task>,
        taskTimerEnabled: Int,
    ) {
        this.taskList = taskList
        this.isSelectionModeEnabled = isSelectionModeEnabled
        this.tasksSelected = tasksSelected
        this.taskTimerEnabled = taskTimerEnabled
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

    private fun updateTaskSelection(task: AdapterItem): SelectionState {
        return if (tasksSelected?.any { it.uid == task.uid } == true) {
            SelectionState.SELECTED
        } else {
            SelectionState.DEFAULT
        }
    }

    private fun updateSwitchVisibility(task: AdapterItem): Boolean {
        return if (isSelectionModeEnabled) task.uid == taskTimerEnabled else true
    }

    private fun updateSwitchState(task: AdapterItem): SwitchState {
        return if (taskTimerEnabled != -1) {
            if (task.uid == taskTimerEnabled) {
                SwitchState.ENABLED
            } else {
                SwitchState.DISABLED
            }
        } else {
            SwitchState.DEFAULT
        }
    }
}