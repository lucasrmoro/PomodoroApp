package br.com.lucas.pomodoroapp.ui.listTaskScreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.lucas.pomodoroapp.core.extensions.convertMinutesToHour
import br.com.lucas.pomodoroapp.core.extensions.getColorResCompat
import br.com.lucas.pomodoroapp.database.Task
import br.com.lucas.pomodoroapp.databinding.ListTaskItemBinding


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

        fun bind(task: ListTaskAdapterItem) {
            addTaskItemProperties(task)

            if (task.isTaskSelected()) {
                checkIconAppearAnimation()
                binding.root.setCardBackgroundColor(cardColorSelected)
                selectionTaskCallback(task, true)
            }

            binding.root.setOnLongClickListener { v ->
                if (v != null) {
                    toggleSelectionMode(task)
                    selectionTaskCallback(task, task.isTaskSelected())
                }
                true
            }
            binding.root.setOnClickListener {
                if (isSelectionModeEnabledCallback(task)) {
                    toggleSelectionMode(task)
                    selectionTaskCallback(task, task.isTaskSelected())
                } else {
                    launchEditScreenCallback(task)
                }
            }
        }

        private fun addTaskItemProperties(task: ListTaskAdapterItem) {
            binding.itemTaskName.text = task.taskName
            binding.itemTaskTime.text = task.taskMinutes.convertMinutesToHour()
            binding.root.setCardBackgroundColor(cardColorDefault)
            configureCheckItem()
            if (task.isTimerSwitchViewVisible) {
                binding.timerSwitch.visibility = View.VISIBLE
            } else {
                binding.timerSwitch.visibility = View.GONE
            }
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
}