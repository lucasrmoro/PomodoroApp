package br.com.lucas.pomodoroapp.ui.editTaskScreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.observe
import br.com.lucas.pomodoroapp.R
import br.com.lucas.pomodoroapp.core.extensions.putInTheRespectiveViews
import br.com.lucas.pomodoroapp.core.extensions.toast
import br.com.lucas.pomodoroapp.database.model.PomodoroDurations
import br.com.lucas.pomodoroapp.database.model.Task
import br.com.lucas.pomodoroapp.databinding.ActivityEditTaskBinding
import br.com.lucas.pomodoroapp.helpers.AlertDialogHelper
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
        var durations = PomodoroDurations()

        if (task != null) {
            viewModel.setup(task)
            durations = task.pomodoroDurations
            binding.edtTaskName.setText(task.taskName)
            binding.toolbar.title = getString(R.string.edit_task_toolbar_label)
        }

        durations.pomodoroTime.putInTheRespectiveViews(
            binding.sliderPomodoroTime, binding.edtPomodoroTime)

        durations.shortBreakTime.putInTheRespectiveViews(
            binding.sliderShortBreakTime, binding.edtShortBreakTime)

        durations.longBreakTime.putInTheRespectiveViews(
            binding.sliderLongBreakTime, binding.edtLongBreakTime)

        durations.numberOfCycles.putInTheRespectiveViews(
            binding.sliderPomodoroTimersCycles, binding.edtPomodoroTimersCycles)

        binding.sliderPomodoroTime.addOnChangeListener { _, value, _ ->
            binding.edtPomodoroTime.text = value.toInt().toString()
        }

        binding.sliderShortBreakTime.addOnChangeListener { _, value, _ ->
            binding.edtShortBreakTime.text = value.toInt().toString()
        }

        binding.sliderLongBreakTime.addOnChangeListener { _, value, _ ->
            binding.edtLongBreakTime.text = value.toInt().toString()
        }

        binding.sliderPomodoroTimersCycles.addOnChangeListener { _, value, _ ->
            binding.edtPomodoroTimersCycles.text = value.toInt().toString()
        }

        binding.edtTaskName.doAfterTextChanged {
            viewModel.checkTaskNameIsValid(it.toString())
            Timber.d("Task name: $it")
        }

        binding.fabSave.setOnClickListener {
            val pomodoroDurations = PomodoroDurations(
                binding.sliderPomodoroTime.value.toInt(),
                binding.sliderShortBreakTime.value.toInt(),
                binding.sliderLongBreakTime.value.toInt(),
                binding.sliderPomodoroTimersCycles.value.toInt(),
            )
            viewModel.onSaveEvent(
                taskName = binding.edtTaskName.text.toString(),
                pomodoroDurations = pomodoroDurations,
                toastOfSuccessUpdate = { toast(R.string.successfully_changed) },
                toastOfSuccessAdd = { toast(R.string.successfully_saved) },
                toastOfFail = { toast(R.string.fill_all_required_fields) },
                closeScreen = { finish() }
            )
        }

        binding.fabSaveAndRun.setOnClickListener {
            toast(R.string.feature_isnt_implemented)
        }

        viewModel.isTaskNameValid.observe(this) { isNameValid ->
            binding.edtTaskNameLayout.error =
                if (!isNameValid) getString(R.string.you_have_to_name_the_task) else null
        }
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
            if (viewModel.isTaskEnabled) {
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
