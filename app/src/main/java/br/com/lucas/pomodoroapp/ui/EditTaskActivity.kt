package br.com.lucas.pomodoroapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.observe
import br.com.lucas.pomodoroapp.databinding.ActivityEditTaskBinding
import com.google.android.material.timepicker.MaterialTimePicker.Builder
import com.google.android.material.timepicker.TimeFormat

class EditTaskActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditTaskBinding

    lateinit var viewModel: EditTaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        viewModel = EditTaskViewModel()
        setContentView(binding.root)

        binding.editTimer.setOnClickListener() {
            showTimePicker()
        }

        binding.editTimer.doAfterTextChanged {
            Log.d("log test editTimer", it.toString())
        }

        binding.editTask.doAfterTextChanged {
            viewModel.validTask(it.toString())
            Log.d("log test editTask", it.toString())
        }


        binding.saveButton.setOnClickListener {
            viewModel.onSaveEvent(context = this, taskName = binding.editTask.text.toString() )
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
    }

    override fun onResume() {
        super.onResume()
        binding.editTask.requestFocus()
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
            binding.editTimer.setText(" ${picker.hour} : ${picker.minute}")
        }
    }
    companion object{
        fun launch(context: Context){
            val intent = Intent(context, EditTaskActivity::class.java)
            context.startActivity(intent)
        }
    }
}

