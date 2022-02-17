package br.com.lucas.pomodoroapp.ui.listTaskScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.lucas.pomodoroapp.core.extensions.convertMinutesToHour
import br.com.lucas.pomodoroapp.core.extensions.getColorResCompat
import br.com.lucas.pomodoroapp.databinding.ListTaskItemBinding

class ListTaskAdapter(
    private val listTaskAdapterEvents: ListTaskAdapterEvents
) : ListAdapter<AdapterItem, ListTaskAdapter.TaskViewHolder>(DiffCallback()) {

    fun addTask(tasks: List<AdapterItem>) {
        submitList(tasks)
    }

    fun reset() {
        currentList.forEach {
            it.resetTaskSelection()
        }
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

        fun bind(task: AdapterItem) {
            binding.itemTaskName.text = task.taskName
            binding.itemTaskTime.text = task.taskMinutes.convertMinutesToHour()
            cardSelectedState(task.isTaskSelected, false)

            binding.root.setOnLongClickListener { v ->
                if (v != null) {
                    toggleSelectionMode(task)
                }
                true
            }

            binding.root.setOnClickListener {
                if (listTaskAdapterEvents.isSelectionModeEnabledCallback()) {
                    toggleSelectionMode(task)
                } else {
                    listTaskAdapterEvents.launchEditScreenCallback(task)
                }
            }
        }

        private fun toggleSelectionMode(task: AdapterItem) {
            task.toggleTask()
            listTaskAdapterEvents.selectionTaskCallback(task)
            cardSelectedState(task.isTaskSelected, true)
        }

        private fun cardSelectedState(isSelected: Boolean, isAnimated: Boolean) {
            val scale = if(isSelected) 1f else 0f
            val cardColor = if(isSelected) cardColorSelected else cardColorDefault
            binding.root.setCardBackgroundColor(cardColor)
            if(isAnimated){
                binding.checkItem.animate().scaleX(scale).scaleY(scale).duration = 250
            } else {
                binding.checkItem.scaleX = scale
                binding.checkItem.scaleY = scale
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
}