package br.com.lucas.pomodoroapp.ui.editTaskScreen

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.lucas.pomodoroapp.R
import br.com.lucas.pomodoroapp.core.extensions.toast
import br.com.lucas.pomodoroapp.core.receiver.AlarmReceiver
import br.com.lucas.pomodoroapp.database.DataBaseConnect
import br.com.lucas.pomodoroapp.database.Task
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class EditTaskViewModel(application: Application) : AndroidViewModel(application) {

    var total: Int = 25
        private set

    val isPomodoroTimerValid = MutableLiveData<Boolean>()
    val isTaskNameValid = MutableLiveData<Boolean>()
    private val HOUR_ON_MINUTES = 60

    var isEditMode = false
        private set

    var task: Task? = null
        private set

    private val alarmManager: AlarmManager? =
        application.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    private val notifyIntent = Intent(application, AlarmReceiver::class.java)

    fun setup(task: Task) {
        this.task = task
        this.isEditMode = true
        total = task.taskMinutes

        val notifyPendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            BROADCAST_REQUEST_CODE,
            notifyIntent.apply {
                putExtra(AlarmReceiver.TASK_NAME, task?.taskName)
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        Log.d(AlarmReceiver.TAG, "Starting ${task.taskName}")
        alarmManager?.let { manager ->
            // TODO AlarmFeature: Replace 3 minutes for the real task time

            AlarmManagerCompat.setExactAndAllowWhileIdle(
                manager,
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                TimeUnit.MINUTES.toMillis(3),
                notifyPendingIntent
            )
        }
    }

    fun delete(context: Context, closeScreen: () -> Unit) {
        val task = task ?: return
        viewModelScope.launch {
            DataBaseConnect.getTaskDao(context).deleteTask(task)
            closeScreen()
        }
    }

    fun validTask(content: String) {
        isTaskNameValid.value = content.length >= 3
    }

    fun checkTimeIsValid(hour: Int, minute: Int) {
        val hoursInMinutes = hour * HOUR_ON_MINUTES
        total = hoursInMinutes + minute
        checkTotalTime()
    }

    private fun checkTotalTime() {
        isPomodoroTimerValid.value = total in 25..60
    }

    fun onSaveEvent(context: Context, taskName: String, closeScreen: (() -> Unit)) {
        if (task == null) {
            saveNewTask(context, taskName, closeScreen)
        } else {
            task!!.taskName = taskName
            task!!.taskMinutes = total
            validTask(task!!.taskName)
            checkTotalTime()
            saveSameTask(context, task!!, closeScreen)
        }
    }

    private fun saveSameTask(
        context: Context,
        task: Task,
        closeScreen: () -> Unit
    ) {
        if (isPomodoroTimerValid.value == true && isTaskNameValid.value == true) {
            viewModelScope.launch {
                DataBaseConnect.getTaskDao(context).updateTask(
                    task
                )
                context.toast(R.string.successfully_saved)
                closeScreen()
            }
        } else {
            context.toast(R.string.fill_all_required_fields)
        }
    }

    private fun saveNewTask(
        context: Context,
        taskName: String,
        closeScreen: () -> Unit
    ) {
        if (isPomodoroTimerValid.value == true && isTaskNameValid.value == true) {
            viewModelScope.launch {
                DataBaseConnect.getTaskDao(context).insertTask(
                    Task(
                        taskName = taskName,
                        taskMinutes = total,
                        uid = 0
                    )
                )
                context.toast(R.string.successfully_saved)
                closeScreen()
            }
        } else {
            context.toast(R.string.fill_all_required_fields)
        }
    }

    companion object {
        private const val BROADCAST_REQUEST_CODE = 123
    }

}