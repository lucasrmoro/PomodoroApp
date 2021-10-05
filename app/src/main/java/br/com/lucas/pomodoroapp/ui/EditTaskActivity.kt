package br.com.lucas.pomodoroapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.observe
import br.com.lucas.pomodoroapp.R
import br.com.lucas.pomodoroapp.R.string.*
import br.com.lucas.pomodoroapp.core.extensions.convertMinutesToHour
import br.com.lucas.pomodoroapp.core.extensions.getColorResCompat
import br.com.lucas.pomodoroapp.core.extensions.toast
import br.com.lucas.pomodoroapp.database.Task
import br.com.lucas.pomodoroapp.databinding.ActivityEditTaskBinding
import br.com.lucas.pomodoroapp.helpers.AlertDialogHelper
import com.google.android.material.timepicker.MaterialTimePicker.Builder
import com.google.android.material.timepicker.TimeFormat

class EditTaskActivity() : AppCompatActivity() {

    lateinit var binding: ActivityEditTaskBinding

    lateinit var viewModel: EditTaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        viewModel = EditTaskViewModel()
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val task: Task? = intent.getSerializableExtra(TASK_NAME_KEY) as? Task

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
            Log.d("log test editTimer", it.toString())
        }

        binding.editTaskName.doAfterTextChanged {
            viewModel.validTask(it.toString())
            Log.d("log test editTask", it.toString())
        }

        binding.fabSave.setOnClickListener {
            viewModel.onSaveEvent(context = this,
                taskName = binding.editTaskName.text.toString(),
                closeScreen = { finish() })
        }

        binding.fabSaveAndRun.setOnClickListener {
            toast(feature_isnt_implemented)
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
                toast(select_valid_time, 3500)
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
            deleteMenu?.title = getString(yes)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_delete_action) {
            deleteTask()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteTask() {
        AlertDialogHelper.show(

            context = this,
            title = are_you_sure,
            bodyMessage = delete_confirmation_message,
            positiveButtonMessage = yes,
            positiveButtonAction = {
                try {
                    viewModel.delete(this) {
                        toast(successfully_deleted)
                        finish()
                    }
                } catch (e: Exception) {
                    toast(somenthing_went_wrong)
                    Log.e("exception", "${e.message}")
                }
            },
            negativeButtonMessage = cancel
        )
    }

    @SuppressLint("SetTextI18n")
    private fun showTimePicker() {
        val picker = Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(0)
            .setMinute(25)
            .setTitleText(getString(R.string.select_pomodoro_time))
            .build()
        picker.show(supportFragmentManager, "Test")
        picker.addOnPositiveButtonClickListener {
            viewModel.checkTimeIsValid(picker.hour, picker.minute)
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

        private const val TASK_NAME_KEY = "task"
    }
}
