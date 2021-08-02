package br.com.lucas.pomodoroapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import br.com.lucas.pomodoroapp.core.extensions.convertMinutesToHour
import br.com.lucas.pomodoroapp.database.Task
import br.com.lucas.pomodoroapp.databinding.ListTaskItemBinding

class ListTaskAdapter : Adapter<ListTaskAdapter.TaskViewHolder>() {

    private val tasks = mutableListOf<Task>()

    fun addTask(tasks: List<Task>) {
        this.tasks.clear()
        this.tasks.addAll(tasks)
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

    class TaskViewHolder(private val binding: ListTaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.itemTaskName.text = task.taskName
            binding.itemTaskTime.text = task.taskMinutes.convertMinutesToHour()
            binding.root.setOnLongClickListener { v ->
                if (v != null) {
                    Toast.makeText(v.context, "LONG PRESS", Toast.LENGTH_LONG).show()
                }
                true
            }
        }
    }
}