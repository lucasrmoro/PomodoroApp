package br.com.lucas.pomodoroapp.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.lucas.pomodoroapp.core.extensions.OnItemClickListener
import br.com.lucas.pomodoroapp.core.extensions.addOnItemClickListener
import br.com.lucas.pomodoroapp.databinding.ActivityListTaskBinding

class ListTaskActivity : AppCompatActivity() {

    lateinit var binding: ActivityListTaskBinding

    lateinit var viewModel: ListTaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListTaskBinding.inflate(layoutInflater)
        viewModel = ListTaskViewModel(application)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        viewModel.taskList.observe(
            this
        ) { tasks ->
            (binding.recyclerView.adapter as ListTaskAdapter).addTask(tasks)
        }

        viewModel.isSelectedModeEnabled.observe(
            this
        ) {
            if(it){
                //Show the trash
                Toast.makeText(this, "Selection mode enabled", Toast.LENGTH_SHORT).show()
                //Change the click event to the same like the long press
            } else {
                //Hide the trash
                Toast.makeText(this, "Selection mode disabled", Toast.LENGTH_SHORT).show()
            }
        }

        binding.fab.setOnClickListener { view ->
            EditTaskActivity.launchNewTaskScreen(this)
        }
        configureList(this)
    }

    private fun configureList(context: Context) {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = ListTaskAdapter() { task, isSelected ->
            Log.d("taskselection", "task: ${task.taskName} --> isSelected: $isSelected")
            viewModel.syncSelection(task, isSelected)
        }
        binding.recyclerView.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                val task = viewModel.findTaskByPosition(position)
                EditTaskActivity.launchEditTaskScreen(context, task = task)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }
}