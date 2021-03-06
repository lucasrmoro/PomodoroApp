package br.com.lucas.pomodoroapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import br.com.lucas.pomodoroapp.R
import br.com.lucas.pomodoroapp.core.extensions.convertMinutesToHour
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

    fun reset(){
        this.tasks.forEach{
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
        fun bind(task: Task) {
            binding.itemTaskName.text = task.taskName
            binding.itemTaskTime.text = task.taskMinutes.convertMinutesToHour()
            binding.checkItem.isVisible = task.isTaskSelected()
            binding.root.setOnLongClickListener { v ->
                if (v != null) {
                    Toast.makeText(v.context, "LONG PRESS", Toast.LENGTH_SHORT).show()
                    toggleSelectionMode(task)
                    selectionTaskCallback(task, task.isTaskSelected())
                }
                true
            }
            binding.root.setOnClickListener { v ->
                if(isSelectionModeEnabledCallback(task)) {
                    toggleSelectionMode(task)
                    selectionTaskCallback(task, task.isTaskSelected())
                } else {
                    launchEditScreenCallback(task)
                }
                Toast.makeText(v.context, "SIMPLE CLICK", Toast.LENGTH_SHORT).show()
            }
        }

        private fun toggleSelectionMode(task: Task) {
            val cardColorDefault =
                ContextCompat.getColor(binding.root.context, R.color.card_color_default)
            val cardColorSelected =
                ContextCompat.getColor(binding.root.context, R.color.card_color_selected)
            task.toggleTask()
            binding.checkItem.isVisible = task.isTaskSelected()
            if (task.isTaskSelected()) {
                binding.root.setCardBackgroundColor(cardColorSelected)
            } else {
                binding.root.setCardBackgroundColor(cardColorDefault)
            }
        }
    }
}