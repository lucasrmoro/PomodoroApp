package br.com.lucas.pomodoroapp.ui

import android.os.Bundle
import android.widget.LinearLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.lucas.pomodoroapp.R
import br.com.lucas.pomodoroapp.database.Task
import br.com.lucas.pomodoroapp.databinding.ActivityListTaskBinding

class ListTaskActivity : AppCompatActivity() {

    lateinit var binding: ActivityListTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { view ->
            EditTaskActivity.launch(this)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = ListTaskAdapter()

        val tasks = (1 .. 100).map { Task(it, "Test$it", it * 100)}
        (binding.recyclerView.adapter as ListTaskAdapter).addTask(tasks)
    }
}