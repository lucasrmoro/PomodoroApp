package br.com.lucas.pomodoroapp.ui

import android.os.Bundle
import android.widget.LinearLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.lucas.pomodoroapp.R
import br.com.lucas.pomodoroapp.database.Task
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

        binding.fab.setOnClickListener { view ->
            EditTaskActivity.launch(this)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = ListTaskAdapter()

        // TODO - [EditTaskSupport] 1. Add the code to open a new activity according to the user's click
        // TODO - [EditTaskSupport] 2. Inside of the user click you should call EditTaskActivity.launch(context, task), where the task will be the task that the user selected

    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }
}