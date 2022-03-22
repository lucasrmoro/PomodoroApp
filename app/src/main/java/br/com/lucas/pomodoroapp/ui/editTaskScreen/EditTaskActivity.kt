package br.com.lucas.pomodoroapp.ui.editTaskScreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.observe
import br.com.lucas.pomodoroapp.R
import br.com.lucas.pomodoroapp.core.extensions.convertMinutesToHour
import br.com.lucas.pomodoroapp.core.extensions.getColorResCompat
import br.com.lucas.pomodoroapp.core.extensions.toast
import br.com.lucas.pomodoroapp.database.model.Task
import br.com.lucas.pomodoroapp.databinding.ActivityEditTaskBinding
import br.com.lucas.pomodoroapp.helpers.AlertDialogHelper
import com.google.android.material.timepicker.MaterialTimePicker.Builder
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class EditTaskActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditTaskBinding

    private val viewModel by viewModels<EditTaskViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val task: Task? = intent.getParcelableExtra(TASK_NAME_KEY) as? Task

        if (task != null) {
            viewModel.setup(task)
            binding.toolbar.title = getString(R.string.edit_task_toolbar_label)
            binding.editTaskName.setText("${viewModel.task?.taskName}")
            binding.editPomodoroTimer.text =
                "${viewModel.task?.taskMinutes?.convertMinutesToHour()}"
        }

        binding.editPomodoroTimer.setOnClickListener() {
            showTimePicker()
        }

        binding.editPomodoroTimer.doAfterTextChanged {
            Timber.d("Pomodoro time: $it")
        }

        binding.editTaskName.doAfterTextChanged {
            viewModel.checkTaskNameIsValid(it.toString())
            Timber.d("Task name: $it")
        }

        binding.fabSave.setOnClickListener {
            viewModel.onSaveEvent(
                taskName = binding.editTaskName.text.toString(),
                toastOfSuccessUpdate = { toast(R.string.successfully_changed) },
                toastOfSuccessAdd = { toast(R.string.successfully_saved) },
                toastOfFail = { toast(R.string.fill_all_required_fields) },
                closeScreen = { finish() }
            )
        }

        binding.fabSaveAndRun.setOnClickListener {
            toast(R.string.feature_isnt_implemented)
        }

        viewModel.isTaskNameValid.observe(this) {
            if (it == true) {
                binding.taskName.setTextColor(this.getColorResCompat(android.R.attr.textColorPrimary))
            } else {
                binding.taskName.setTextColor(Color.RED)
            }
        }

        viewModel.isPomodoroTimerValid.observe(this) {
            if (it == false) {
                toast(R.string.select_valid_time, 3500)
                binding.pomodoroTimer.setTextColor(Color.RED)
            } else {
                binding.pomodoroTimer.setTextColor(this.getColorResCompat(android.R.attr.textColorPrimary))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.editTaskName.requestFocus()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (viewModel.isEditMode) {
            menuInflater.inflate(R.menu.delete_menu, menu)
            val deleteMenu = menu?.findItem(R.menu.delete_menu)
            deleteMenu?.title = getString(R.string.yes)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_delete_action) {
            if(viewModel.isTaskEnabled){
                toast(R.string.can_not_delete_task_with_active_timer)
            } else {
                deleteTask()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteTask() {
        AlertDialogHelper.show(
            context = this,
            title = R.string.are_you_sure,
            bodyMessage = R.string.delete_confirmation_message,
            positiveButtonMessage = R.string.yes,
            positiveButtonAction = {
                try {
                    viewModel.delete(
                        toastOfSuccess = { toast(R.string.successfully_deleted) },
                        closeScreen = { finish() }
                    )
                } catch (e: Exception) {
                    toast(R.string.somenthing_went_wrong)
                    Timber.e(e.message)
                }
            },
            negativeButtonMessage = R.string.cancel
        )
    }

    @SuppressLint("SetTextI18n")
    private fun showTimePicker() {
        val picker = Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(0)
            .setMinute(25)
            .setTitleText(R.string.select_pomodoro_time)
            .build()
        picker.show(supportFragmentManager, "Test")
        picker.addOnPositiveButtonClickListener {
            viewModel.checkTaskTimeIsValid(picker.hour, picker.minute)
            binding.editPomodoroTimer.text = viewModel.total.convertMinutesToHour()
        }
    }

    companion object {
        fun launchNewTaskScreen(context: Context) {
            val intent = Intent(context, EditTaskActivity::class.java)
            context.startActivity(intent)
        }

        fun launchEditTaskScreen(context: Context, task: Task?) {
            val intent = Intent(context, EditTaskActivity::class.java)
            intent.putExtra(TASK_NAME_KEY, task)
            context.startActivity(intent)
        }

        private const val TASK_NAME_KEY = "Task"
    }
}
