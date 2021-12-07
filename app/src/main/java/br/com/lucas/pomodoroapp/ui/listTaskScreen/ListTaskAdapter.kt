package br.com.lucas.pomodoroapp.ui.listTaskScreen

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.AlarmManagerCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.lucas.pomodoroapp.core.extensions.convertMinutesToHour
import br.com.lucas.pomodoroapp.core.extensions.getColorResCompat
import br.com.lucas.pomodoroapp.core.receiver.AlarmReceiver
import br.com.lucas.pomodoroapp.databinding.ListTaskItemBinding
import java.util.concurrent.TimeUnit


class ListTaskAdapter(
    private val selectionTaskCallback: ((ListTaskAdapterItem, Boolean) -> Unit),
    private val isSelectionModeEnabledCallback: ((ListTaskAdapterItem) -> Boolean),
    private val launchEditScreenCallback: ((ListTaskAdapterItem) -> Unit)
) : ListAdapter<ListTaskAdapterItem, ListTaskAdapter.TaskViewHolder>(DiffCallback()) {

    fun addTask(tasks: List<ListTaskAdapterItem>) {
        submitList(tasks)
    }

    fun hideAllTimerSwitches() {
        val newList = currentList.map {
            it.copy(isTimerSwitchViewVisible = false)
        }
        submitList(newList)
    }

    fun selectedTaskIds() = currentList
        .filter { it.isTaskSelected() }
        .map { it.uid }
        .toList()

    fun reset() {
        val newList = currentList.map {
            it.resetTaskSelection()
            it.copy(isTimerSwitchViewVisible = true)
        }
        submitList(newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            ListTaskItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    inner class TaskViewHolder(private val binding: ListTaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val cardColorDefault =
            binding.root.context.getColorResCompat(android.R.attr.colorBackground)
        private val cardColorSelected =
            binding.root.context.getColorResCompat(android.R.attr.colorControlHighlight)

        private val alarmManager: AlarmManager? =
            binding.root.context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        private val hasPermission: Boolean? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager?.canScheduleExactAlarms()
        } else {
            true
        }
        private val pendingIntentFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        fun bind(task: ListTaskAdapterItem) {
            addTaskItemProperties(task)
            val requestAlarmPermissionIntent = Intent().apply {
                action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
            }

            binding.root.setOnLongClickListener { v ->
                if (v != null) {
                    toggleSelectionMode(task)
                    selectionTaskCallback(task, task.isTaskSelected())
                }
                true
            }
            binding.root.setOnClickListener { v ->
                if (v != null) {
                    if (isSelectionModeEnabledCallback(task)) {
                        toggleSelectionMode(task)
                        selectionTaskCallback(task, task.isTaskSelected())
                    } else {
                        launchEditScreenCallback(task)
                    }
                }
            }

            binding.timerSwitch.setOnCheckedChangeListener { _, isChecked ->
                task.isPomodoroTimerEnabled = isChecked
                val notifyIntent = Intent(binding.root.context, AlarmReceiver::class.java).apply {
                    if (task.isPomodoroTimerEnabled == true) this.putExtra(AlarmReceiver.TASK_NAME,
                        task.taskName) else this.extras?.clear()
                }
                val notifyPendingIntent = PendingIntent.getBroadcast(
                    binding.root.context,
                    BROADCAST_REQUEST_CODE,
                    notifyIntent,
                    pendingIntentFlag
                )
                when (isChecked) {
                    true -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            if (hasPermission!!) {
                                startAlarm(task, notifyPendingIntent)
                            } else {
                                binding.root.context.startActivity(requestAlarmPermissionIntent)
                            }
                        } else {
                            startAlarm(task, notifyPendingIntent)
                        }
                        Log.d(AlarmReceiver.TAG, "Starting ${task.taskName}")
                        Log.d(AlarmReceiver.TAG, "task time: ${task.taskMinutes}")
                        disableAllSwitches()
                    }
                    false -> {
                        enableAllSwitches()
                        alarmManager?.cancel(notifyPendingIntent)
                        Log.d(AlarmReceiver.TAG, "Canceling ${task.taskName}")
                        Log.d(AlarmReceiver.TAG, "task time: ${task.taskMinutes}")
                    }
                }
            }
        }

        private fun startAlarm(
            task: ListTaskAdapterItem,
            notifyPendingIntent: PendingIntent
        ) {
            alarmManager?.let { manager ->
                AlarmManagerCompat.setExactAndAllowWhileIdle(
                    manager,
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() +
                            TimeUnit.MINUTES.toMillis(task.taskMinutes * 1L),
                    notifyPendingIntent
                )
            }
        }

        private fun addTaskItemProperties(task: ListTaskAdapterItem) {
            binding.itemTaskName.text = task.taskName
            binding.itemTaskTime.text = task.taskMinutes.convertMinutesToHour()
            binding.timerSwitch.isEnabled = task.isPomodoroTimerEnabled ?: true
            binding.root.setCardBackgroundColor(cardColorDefault)
            configureCheckItem()
            if (task.isTaskSelected()) {
                checkIconAppearAnimation()
                binding.root.setCardBackgroundColor(cardColorSelected)
                selectionTaskCallback(task, true)
            }
            if (task.isTimerSwitchViewVisible) {
                binding.timerSwitch.visibility = View.VISIBLE
            } else {
                binding.timerSwitch.visibility = View.GONE
            }
        }

        private fun disableAllSwitches() {
            val newList = currentList
                .map { task ->
                    if (task.isPomodoroTimerEnabled == true) {
                        task
                    } else {
                        task.copy(isPomodoroTimerEnabled = false)
                    }
                }
            submitList(newList)
        }

        private fun enableAllSwitches() {
            val newList = currentList
                .map { task ->
                    if (task.isPomodoroTimerEnabled == false) {
                        task.copy(isPomodoroTimerEnabled = null)
                    } else {
                        task
                    }
                }
            submitList(newList)
        }

        private fun configureCheckItem() {
            binding.checkItem.scaleX = 0f
            binding.checkItem.scaleY = 0f
        }

        private fun toggleSelectionMode(task: ListTaskAdapterItem) {
            task.toggleTask()
            if (task.isTaskSelected()) {
                checkIconAppearAnimation()
                binding.root.setCardBackgroundColor(cardColorSelected)
            } else {
                checkIconDisappearAnimation()
                binding.root.setCardBackgroundColor(cardColorDefault)
            }
        }

        private fun checkIconAppearAnimation() {
            binding.checkItem.animate().scaleX(1f).scaleY(1f).duration = 250
        }

        private fun checkIconDisappearAnimation() {
            binding.checkItem.animate().scaleX(0f).scaleY(0f).duration = 250
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ListTaskAdapterItem>() {
        override fun areItemsTheSame(
            oldItem: ListTaskAdapterItem,
            newItem: ListTaskAdapterItem
        ): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(
            oldItem: ListTaskAdapterItem,
            newItem: ListTaskAdapterItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val BROADCAST_REQUEST_CODE = 123
    }
}