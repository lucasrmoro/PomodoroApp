package br.com.lucas.pomodoroapp.ui

import android.os.Handler
import android.os.Looper.getMainLooper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import br.com.lucas.pomodoroapp.core.extensions.convertMinutesToHour
import br.com.lucas.pomodoroapp.core.extensions.getColorResCompat
import br.com.lucas.pomodoroapp.database.Task
import br.com.lucas.pomodoroapp.databinding.ListTaskItemBinding


class ListTaskAdapter(
    private val selectionTaskCallback: ((Task, Boolean) -> Unit),
    private val isSelectionModeEnabledCallback: ((Task) -> Boolean),
    private val launchEditScreenCallback: ((Task) -> Unit)
) : Adapter<ListTaskAdapter.TaskViewHolder>() {

    private val tasks = mutableListOf<Task>()

    fun addTask(tasks: List<Task>) {
        this.tasks.clear()
        this.tasks.addAll(tasks)
        notifyDataSetChanged()
    }

    fun reset() {
        this.tasks.forEach {
            it.resetTaskSelection()
        }
        notifyDataSetChanged()
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
        val task = tasks[position]
        holder.bind(task)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    inner class TaskViewHolder(private val binding: ListTaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val cardColorDefault =
            binding.root.context.getColorResCompat(android.R.attr.colorBackground)
        private val cardColorSelected =
            binding.root.context.getColorResCompat(android.R.attr.colorControlHighlight)

        fun bind(task: Task) {
            addTaskItemProperties(task)
            binding.root.setOnLongClickListener { v ->
                if (v != null) {
                    toggleSelectionMode(task)
                    selectionTaskCallback(task, task.isTaskSelected())
                }
                true
            }
            binding.root.setOnClickListener { v ->
                if (isSelectionModeEnabledCallback(task)) {
                    toggleSelectionMode(task)
                    selectionTaskCallback(task, task.isTaskSelected())
                } else {
                    launchEditScreenCallback(task)
                }
            }
        }

        private fun addTaskItemProperties(task: Task) {
            binding.itemTaskName.text = task.taskName
            binding.itemTaskTime.text = task.taskMinutes.convertMinutesToHour()
            binding.root.setCardBackgroundColor(cardColorDefault)
            configureCheckItem()
        }

        private fun configureCheckItem() {
            binding.checkItem.scaleX = 0f
            binding.checkItem.scaleY = 0f
        }

        private fun toggleSelectionMode(task: Task) {
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
}