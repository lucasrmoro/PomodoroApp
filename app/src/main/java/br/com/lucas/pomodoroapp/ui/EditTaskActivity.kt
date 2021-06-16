package br.com.lucas.pomodoroapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.nfc.Tag
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.observe
import br.com.lucas.pomodoroapp.database.Task
import br.com.lucas.pomodoroapp.databinding.ActivityEditTaskBinding
import com.google.android.material.timepicker.MaterialTimePicker.Builder
import com.google.android.material.timepicker.TimeFormat
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class EditTaskActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditTaskBinding

    lateinit var viewModel: EditTaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        viewModel = EditTaskViewModel()
        setContentView(binding.root)


        val task: Task? = null
        // TODO - [EditTaskSupport] 4. Unwrap the intent to get the Task object

        if (task != null) {
            viewModel.setup(task)
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

        viewModel.isTaskNameValid.observe(this) {
            if (it == true) {
                binding.taskName.setTextColor(Color.BLACK)
            } else {
                binding.taskName.setTextColor(Color.RED)
            }
        }

        viewModel.isPomodoroTimerValid.observe(this) {
            if (it == false) {
                Toast.makeText(
                    this,
                    "Select a valid time between 25 minutes and 1 hour",
                    Toast.LENGTH_LONG
                )
                    .show()
                binding.pomodoroTimer.setTextColor(Color.RED)
            } else {
                binding.pomodoroTimer.setTextColor(Color.BLACK)
            }
        }

        viewModel.onTaskAlreadyExist.observe(this) { task ->
            // TODO - [EditTaskSupport] 5. Show all values from your task to the UI
        }
    }

    override fun onResume() {
        super.onResume()
        binding.editTaskName.requestFocus()
    }


    @SuppressLint("SetTextI18n")
    private fun showTimePicker() {
        val picker = Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(0)
            .setMinute(25)
            .setTitleText("SELECT POMODORO TIME")
            .build()
        picker.show(supportFragmentManager, "Test")
        picker.addOnPositiveButtonClickListener {
            viewModel.checkTimeIsValid(picker.hour, picker.minute)
            binding.editPomodoroTimer.setText(" ${picker.hour} : ${picker.minute}")
        }
    }

    companion object {
        fun launchNewTaskScreen(context: Context) {
            val intent = Intent(context, EditTaskActivity::class.java)
            context.startActivity(intent)
        }

        fun launchEditTaskScreen(context: Context, task: Task?) {
        // TODO - [EditTaskSupport] 3. Create an intent to open the EditTaskActivity (the same as the previous launch), and passing an object as parameter
            val intent = Intent(context, EditTaskActivity::class.java).apply {
                putExtra("edit_task", task as Serializable)
            }
            context.startActivity(intent)
        }
    }
}
