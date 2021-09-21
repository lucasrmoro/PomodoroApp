package br.com.lucas.pomodoroapp.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.lucas.pomodoroapp.R
import br.com.lucas.pomodoroapp.databinding.ActivityListTaskBinding


class ListTaskActivity : AppCompatActivity() {

    lateinit var binding: ActivityListTaskBinding

    lateinit var viewModel: ListTaskViewModel

    lateinit var adapter: ListTaskAdapter

    private var menu: Menu? = null

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

        viewModel.selectionMode.observe(
            this
        ) { selectionMode ->
            changeTrashVisibilityBasedOnSelectionMode()
            if (!selectionMode) adapter.reset()
        }

        binding.fab.setOnClickListener { view ->
            EditTaskActivity.launchNewTaskScreen(this)
        }

        configureList(this)
    }

    private fun changeTrashVisibilityBasedOnSelectionMode() {
        this.menu?.findItem(R.id.menu_delete_action)?.isVisible =
            viewModel.selectionMode.value == true
    }

    private fun configureList(context: Context) {
        adapter = ListTaskAdapter(
            selectionTaskCallback = { task, isSelected ->
                Log.d("taskselection", "task: ${task.taskName} --> isSelected: $isSelected")
                viewModel.syncSelection(task, isSelected)
            },
            isSelectionModeEnabledCallback = { viewModel.isSelectedModeEnabled() },
            launchEditScreenCallback = { EditTaskActivity.launchEditTaskScreen(context, it) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.delete_menu, menu)
        this.menu = menu
        this.menu?.findItem(R.id.menu_delete_action)?.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menu_delete_action) {
            viewModel.deleteTasks(this)
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }
}