package br.com.lucas.pomodoroapp.ui.listTaskScreen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.lucas.pomodoroapp.BuildConfig
import br.com.lucas.pomodoroapp.R
import br.com.lucas.pomodoroapp.R.anim.from_bottom_anim
import br.com.lucas.pomodoroapp.R.anim.to_bottom_anim
import br.com.lucas.pomodoroapp.R.drawable.ic_close
import br.com.lucas.pomodoroapp.R.drawable.ic_skull
import br.com.lucas.pomodoroapp.R.string.*
import br.com.lucas.pomodoroapp.core.extensions.loadAnim
import br.com.lucas.pomodoroapp.core.extensions.toast
import br.com.lucas.pomodoroapp.core.extensions.toggleFabAnimation
import br.com.lucas.pomodoroapp.core.extensions.toggleFabImage
import br.com.lucas.pomodoroapp.databinding.ActivityListTaskBinding
import br.com.lucas.pomodoroapp.helpers.AlertDialogHelper
import br.com.lucas.pomodoroapp.ui.editTaskScreen.EditTaskActivity


class ListTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListTaskBinding

    private lateinit var viewModel: ListTaskViewModel

    private lateinit var adapter: ListTaskAdapter

    private var deleteMenu: Menu? = null

    private var clicked = false

    private val fromBottom: Animation by lazy { loadAnim(this, from_bottom_anim) }

    private val toBottom: Animation by lazy { loadAnim(this, to_bottom_anim) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListTaskBinding.inflate(layoutInflater)
        viewModel = ListTaskViewModel(application)
        setContentView(binding.root)

        savedInstanceState?.getIntegerArrayList(SELECTED_ELEMENTS_KEY)?.let { previousSelection ->
            viewModel.processPreviousSelection(previousSelection)
        }

        viewModel.taskList.observe(
            this
        ) { tasks ->
            adapter.addTask(tasks)
        }

        viewModel.selectionMode.observe(
            this
        ) { selectionMode ->
            changeTrashVisibilityBasedOnSelectionMode()
            if (!selectionMode){
                adapter.reset()
            } else {
                adapter.hideAllTimerSwitches()
            }
        }

        binding.addFab.setOnClickListener {
            EditTaskActivity.launchNewTaskScreen(this)
        }

        if (BuildConfig.DEBUG) setupDebugButtons()

        configureList(this)
    }

    private fun configureList(context: Context) {
        adapter = ListTaskAdapter(
            selectionTaskCallback = { task, isSelected ->
                viewModel.syncSelection(task, isSelected)
            },
            isSelectionModeEnabledCallback = { viewModel.isSelectedModeEnabled() },
            launchEditScreenCallback = {
                EditTaskActivity.launchEditTaskScreen(context,
                    viewModel.convertTaskAdapterItemToTask(it))
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupDebugButtons() {
        binding.debugFab.visibility = View.VISIBLE

        binding.debugFab.setOnClickListener {
            onButtonClicked()
        }

        binding.debugAddTasksFab.setOnClickListener {
            addTenTasksAutomatically()
            onButtonClicked()
        }
    }

    private fun onButtonClicked() {
        binding.debugAddTasksFab.isVisible = clicked
        binding.debugAddTasksFab.toggleFabAnimation(clicked, fromBottom, toBottom)
        binding.debugFab.toggleFabImage(clicked, ic_skull, ic_close)
        binding.debugAddTasksFab.isClickable = !clicked
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

    private fun changeTrashVisibilityBasedOnSelectionMode() {
        this.deleteMenu?.findItem(R.id.menu_delete_action)?.isVisible =
            viewModel.selectionMode.value == true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.delete_menu, menu)
        this.deleteMenu = menu
        val deleteMenu = menu?.findItem(R.id.menu_delete_action)
        deleteMenu?.isVisible = viewModel.isSelectedModeEnabled()
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
                    toast(successfully_deleted)
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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putIntegerArrayList(SELECTED_ELEMENTS_KEY, ArrayList(adapter.selectedTaskIds()))

        super.onSaveInstanceState(outState)
    }

    companion object {
        private const val SELECTED_ELEMENTS_KEY = "selected elements - adapter's list"
    }
}