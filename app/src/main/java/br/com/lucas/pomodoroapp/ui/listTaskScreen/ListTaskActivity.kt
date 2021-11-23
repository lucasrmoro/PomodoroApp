package br.com.lucas.pomodoroapp.ui.listTaskScreen

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
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
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ListTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListTaskBinding

    private val viewModel by viewModels<ListTaskViewModel>()

    private lateinit var adapter: ListTaskAdapter

    private var deleteMenu: Menu? = null

    private var clicked = false

    private val fromBottom: Animation by lazy { loadAnim(this, from_bottom_anim) }

    private val toBottom: Animation by lazy { loadAnim(this, to_bottom_anim) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.taskList.observe(
            this
        ) { tasks ->
            adapter.addTask(tasks)
        }

        viewModel.selectionMode.observe(
            this
        ) { selectionMode ->
            changeTrashVisibilityBasedOnSelectionMode()
            if (!selectionMode) adapter.reset()
        }

        binding.addFab.setOnClickListener {
            EditTaskActivity.launchNewTaskScreen(this)
        }

        if (viewModel.isDebugMode()) {
            setupDebugButtons()
        }

        configureList(this)
    }

    private fun configureList(context: Context) {
        val listTaskAdapterEvents = object : ListTaskAdapterEvents {
            override fun selectionTaskCallback(adapterItem: AdapterItem) {
                viewModel.syncSelection(adapterItem)
            }

            override fun isSelectionModeEnabledCallback() = viewModel.isSelectedModeEnabled()

            override fun launchEditScreenCallback(adapterItem: AdapterItem) {
                EditTaskActivity.launchEditTaskScreen(context,
                    viewModel.convertAdapterItemToTask(adapterItem))
            }
        }
        adapter = ListTaskAdapter(listTaskAdapterEvents)
        binding.recyclerView.apply{
            itemAnimator = null
            layoutManager = LinearLayoutManager(context)
            adapter = this@ListTaskActivity.adapter
        }
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
                R.plurals.delete_selected_tasks,
                viewModel.getQuantityOfSelectedTasks(),
                viewModel.getQuantityOfSelectedTasks()),
            positiveButtonMessage = yes,
            positiveButtonAction = {
                try {
                    toast(successfully_deleted)
                    viewModel.deleteTasks()
                } catch (e: Exception) {
                    toast(somenthing_went_wrong)
                    Timber.tag("Exception").e(e)
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