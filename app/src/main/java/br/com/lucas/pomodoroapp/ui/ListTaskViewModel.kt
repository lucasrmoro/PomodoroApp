package br.com.lucas.pomodoroapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.lucas.pomodoroapp.database.DataBaseConnect
import br.com.lucas.pomodoroapp.database.Task
import kotlinx.coroutines.launch

class ListTaskViewModel(private val context: Application) : AndroidViewModel(context) {

    val taskList = MutableLiveData<List<Task>>()
    val isSelectedModeEnabled = MutableLiveData<Boolean>(false)
    val selectedTasks = ArrayList<Task>()

    fun syncSelection(task: Task, isSelected: Boolean) {
        if (isSelected) {
            val exists = selectedTasks.any { it.taskName == task.taskName }
            if (!exists) {
                selectedTasks.add(task)
            }
        } else {
            selectedTasks.remove(task)
        }

        if (selectedTasks.isNotEmpty() != isSelectedModeEnabled.value) {
            isSelectedModeEnabled.value = selectedTasks.isNotEmpty()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            taskList.postValue(
                DataBaseConnect.getTaskDao(context).getAll()
            )
        }
    }

    fun findTaskByPosition(position: Int) = taskList.value?.get(position)
}