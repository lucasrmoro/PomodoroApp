package br.com.lucas.pomodoroapp.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.lucas.pomodoroapp.R
import br.com.lucas.pomodoroapp.databinding.ActivityListTaskBinding


class ListTaskActivity : AppCompatActivity() {

    lateinit var binding: ActivityListTaskBinding

    lateinit var viewModel: ListTaskViewModel

    lateinit var adapter: ListTaskAdapter

    private var menu: Menu? = null

    private var clicked = false

    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_bottom_anim
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListTaskBinding.inflate(layoutInflater)
        viewModel = ListTaskViewModel(application)
        setContentView(binding.root)

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

        if (viewModel.isDebugMode()) {
            setupDebugButtons()
        }

        configureList(this)
    }

    private fun setupDebugButtons() {
        setDebugFabVisible()

        binding.debugFab.setOnClickListener {
            onButtonClicked()
        }

        binding.debugAddTasksFab.setOnClickListener {
            addTenTasksAutomatically()
            onButtonClicked()
        }
    }

    private fun setDebugFabVisible() {
        binding.debugFab.visibility = View.VISIBLE
    }

    private fun addTenTasksAutomatically() {
        try {
            viewModel.addTenTasksOnDataBase()
            Toast.makeText(
                this,
                getString(R.string.successfully_ten_tasks_added),
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                this,
                getString(R.string.somenthing_went_wrong),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun onButtonClicked() {
        setVisibility()
        setAnimation()
        changeDebugIcon()
        setClickable()
        clicked = !clicked
    }

    private fun setVisibility() {
        binding.debugAddTasksFab.isVisible = clicked
    }

    private fun changeDebugIcon() {
        if (!clicked)
            binding.debugFab.setImageResource(R.drawable.ic_close)
        else
            binding.debugFab.setImageResource(R.drawable.ic_skull)
    }

    private fun setAnimation() {
        if (!clicked)
            binding.debugAddTasksFab.startAnimation(fromBottom)
        else
            binding.debugAddTasksFab.startAnimation(toBottom)
    }

    private fun setClickable() {
        binding.debugAddTasksFab.isClickable = !clicked
    }

    private fun changeTrashVisibilityBasedOnSelectionMode() {
        this.menu?.findItem(R.id.menu_delete_action)?.isVisible =
            viewModel.selectionMode.value == true
    }

    private fun configureList(context: Context) {
        adapter = ListTaskAdapter(
            selectionTaskCallback = { task, isSelected ->
                Log.d("taskSelection", "task: ${task.taskName} --> isSelected: $isSelected")
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
            setupConfirmationDialog()
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun setupConfirmationDialog() {
        var builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.confirm_delete))
        builder.setMessage(
            viewModel.setupConfirmationDialogMessage(this)
        )
        builder.setPositiveButton(
            getString(R.string.delete)
        ) { dialog, _ ->
            try {
                viewModel.deleteTasks(this)
                Toast.makeText(
                    this,
                    getString(R.string.successfully_deleted),
                    Toast.LENGTH_SHORT
                ).show()
                dialog.cancel()
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    getString(R.string.somenthing_went_wrong),
                    Toast.LENGTH_LONG
                )
                    .show()
                Log.e("deleteError", "${e.message}")
            }
        }
        builder.setNegativeButton(
            getString(R.string.cancel)
        ) { dialog, _ ->
            dialog.cancel()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }
}