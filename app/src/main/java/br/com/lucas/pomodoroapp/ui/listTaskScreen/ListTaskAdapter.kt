package br.com.lucas.pomodoroapp.ui.listTaskScreen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.lucas.pomodoroapp.core.extensions.convertMinutesToHour
import br.com.lucas.pomodoroapp.core.extensions.display
import br.com.lucas.pomodoroapp.core.extensions.getColorResCompat
import br.com.lucas.pomodoroapp.core.extensions.isNotCheckedByHuman
import br.com.lucas.pomodoroapp.databinding.ListTaskItemBinding

class ListTaskAdapter(
    private val listTaskAdapterEvents: ListTaskAdapterEvents,
) : ListAdapter<AdapterItem, ListTaskAdapter.TaskViewHolder>(DiffCallback()) {

    fun addTask(tasks: List<AdapterItem>) {
        submitList(tasks)
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

        @SuppressLint("ResourceType")
        fun bind(task: AdapterItem) {
            binding.itemTaskName.text = task.taskName
            binding.itemTaskTime.text = task.pomodoroDurations.pomodoroTime.convertMinutesToHour()
            binding.checkItem.scaleX = task.selectionState.scale
            binding.checkItem.scaleY = task.selectionState.scale
            binding.root.setBackgroundColor(binding.root.context.getColorResCompat(task.selectionState.backgroundColor))
            binding.timerSwitch.display(task.isSwitchVisible)

            val timerSwitchListener =
                CompoundButton.OnCheckedChangeListener { v, isChecked ->
                    if (v != null) {
                        listTaskAdapterEvents.timerTaskCallback(task, isChecked)
                    }
                }

            if (task.isSwitchVisible) {
                binding.timerSwitch.isEnabled = task.switchState.isAvailable
                binding.timerSwitch.isNotCheckedByHuman(task.switchState.isEnabled,
                    timerSwitchListener)
            }

            binding.timerSwitch.setOnCheckedChangeListener(timerSwitchListener)

            binding.root.setOnLongClickListener { v ->
                if (v != null) {
                    listTaskAdapterEvents.selectionTaskCallback(task)
                }
                true
            }

            binding.root.setOnClickListener {
                if (listTaskAdapterEvents.isSelectionModeEnabledCallback()) {
                    listTaskAdapterEvents.selectionTaskCallback(task)
                } else {
                    listTaskAdapterEvents.launchEditScreenCallback(task)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<AdapterItem>() {
        override fun areItemsTheSame(oldItem: AdapterItem, newItem: AdapterItem): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: AdapterItem, newItem: AdapterItem): Boolean {
            return oldItem == newItem
        }
    }
}

interface ListTaskAdapterEvents {
    fun selectionTaskCallback(adapterItem: AdapterItem)
    fun isSelectionModeEnabledCallback(): Boolean
    fun launchEditScreenCallback(adapterItem: AdapterItem)
    fun timerTaskCallback(adapterItem: AdapterItem, isTimerEnabled: Boolean)
}