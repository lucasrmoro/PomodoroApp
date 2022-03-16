package br.com.lucas.pomodoroapp.ui.listTaskScreen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.lucas.pomodoroapp.BuildConfig
import br.com.lucas.pomodoroapp.R
import br.com.lucas.pomodoroapp.R.anim.from_bottom_anim
import br.com.lucas.pomodoroapp.R.anim.to_bottom_anim
import br.com.lucas.pomodoroapp.R.drawable.ic_close
import br.com.lucas.pomodoroapp.R.drawable.ic_skull
import br.com.lucas.pomodoroapp.core.extensions.loadAnim
import br.com.lucas.pomodoroapp.core.extensions.toast
import br.com.lucas.pomodoroapp.core.extensions.toggleFabAnimation
import br.com.lucas.pomodoroapp.core.extensions.toggleFabImage
import br.com.lucas.pomodoroapp.core.receiver.AlarmReceiver
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

    private val alarmFinishReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.refreshStateOfTasks()
        }
    }

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
        ) {
            changeTrashVisibilityBasedOnSelectionMode()
        }

        binding.addFab.setOnClickListener {
            EditTaskActivity.launchNewTaskScreen(this)
        }

        if (BuildConfig.DEBUG) {
            setupDebugButtons()
        }

        configureList(this)
    }

    private fun configureList(context: Context) {
        val listTaskAdapterEvents = object : ListTaskAdapterEvents {
            override fun selectionTaskCallback(adapterItem: AdapterItem) {
                if (adapterItem.switchState != SwitchState.ENABLED) {
                    viewModel.syncSelection(adapterItem)
                } else {
                    toast(getString(R.string.can_not_select_task_with_timer_active))
                }
            }

            override fun isSelectionModeEnabledCallback() = viewModel.isSelectionModeEnabled()

            override fun launchEditScreenCallback(adapterItem: AdapterItem) {
                EditTaskActivity.launchEditTaskScreen(
                    context,
                    viewModel.convertAdapterItemToTask(adapterItem)
                )
            }

            override fun timerTaskCallback(adapterItem: AdapterItem, isTimerEnabled: Boolean) {
                viewModel.syncTaskTimer(adapterItem, isTimerEnabled)
            }
        }
        adapter = ListTaskAdapter(listTaskAdapterEvents)
        binding.recyclerView.apply {
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
            toast(R.string.successfully_ten_tasks_added)
        } catch (e: Exception) {
            toast(R.string.somenthing_went_wrong)
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
        deleteMenu?.isVisible = viewModel.isSelectionModeEnabled()
        deleteMenu?.title = getString(R.string.delete_selected_tasks)
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
            title = R.string.are_you_sure,
            bodyMessage = resources.getQuantityString(
                R.plurals.delete_selected_tasks,
                viewModel.getQuantityOfSelectedTasks(),
                viewModel.getQuantityOfSelectedTasks()),
            positiveButtonMessage = R.string.yes,
            positiveButtonAction = {
                try {
                    toast(R.string.successfully_deleted)
                    viewModel.deleteTasks()
                } catch (e: Exception) {
                    toast(R.string.somenthing_went_wrong)
                    Timber.tag("Exception").e(e)
                }
            },
            negativeButtonMessage = R.string.cancel
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
        LocalBroadcastManager.getInstance(this).registerReceiver(alarmFinishReceiver,
            IntentFilter(AlarmReceiver.ALARM_FINISH_INTENT_ACTION))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(alarmFinishReceiver)
    }
}