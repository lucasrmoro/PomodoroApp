package br.com.lucas.pomodoroapp.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.lucas.pomodoroapp.R
import br.com.lucas.pomodoroapp.R.string.*
import br.com.lucas.pomodoroapp.core.extensions.toast
import br.com.lucas.pomodoroapp.databinding.ActivityListTaskBinding
import br.com.lucas.pomodoroapp.helpers.AlertDialogHelper


class ListTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListTaskBinding

    lateinit var viewModel: ListTaskViewModel

    lateinit var adapter: ListTaskAdapter

    private var deleteMenu: Menu? = null

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

    private fun onButtonClicked() {
        setVisibility()
        setAnimation()
        changeDebugIcon()
        setClickable()
        clicked = !clicked
    }

    private fun addTenTasksAutomatically() {
        try {
            viewModel.addTenTasksOnDataBase()
            toast(successfully_ten_tasks_added)
        } catch (e: Exception) {
            toast(somenthing_went_wrong)
        }
    }

    private fun setVisibility() {
        binding.debugAddTasksFab.isVisible = clicked
    }

    private fun setAnimation() {
        if (!clicked)
            binding.debugAddTasksFab.startAnimation(fromBottom)
        else
            binding.debugAddTasksFab.startAnimation(toBottom)
    }

    private fun changeDebugIcon() {
        if (!clicked)
            binding.debugFab.setImageResource(R.drawable.ic_close)
        else
            binding.debugFab.setImageResource(R.drawable.ic_skull)
    }

    private fun setClickable() {
        binding.debugAddTasksFab.isClickable = !clicked
    }

    private fun changeTrashVisibilityBasedOnSelectionMode() {
        this.deleteMenu?.findItem(R.id.menu_delete_action)?.isVisible =
            viewModel.selectionMode.value == true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.delete_menu, menu)
        this.deleteMenu = menu
        val deleteMenu = menu?.findItem(R.id.menu_delete_action)
        deleteMenu?.isVisible = false
        deleteMenu?.title = getString(delete_selected_tasks)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_delete_action) {
            setupConfirmationDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupConfirmationDialog() {
        AlertDialogHelper.show(
            context = this,
            title = are_you_sure,
            bodyMessage = resources.getQuantityString(
                // Change the number of selected tasks on alert dialog
                R.plurals.delete_selected_tasks,
                viewModel.getQuantityOfSelectedTasks(),
                viewModel.getQuantityOfSelectedTasks()),
            positiveButtonMessage = yes,
            positiveButtonAction = {
                try {
                    viewModel.deleteTasks(this)
                } catch (e: Exception) {
                    toast(somenthing_went_wrong)
                    Log.e("exception", "${e.message}")
                }
            },
            negativeButtonMessage = cancel
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }
}