package br.com.lucas.pomodoroapp.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.lucas.pomodoroapp.databinding.ActivityEditTaskBinding
import com.google.android.material.timepicker.MaterialTimePicker.Builder
import com.google.android.material.timepicker.TimeFormat

class EditTaskActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditTaskBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editTimer.setOnClickListener() {
            showTimePicker()
        }

        binding.saveButton.setOnClickListener {
            val isTaskNameValid = isValid(
                binding.taskName, binding.editTask.text.toString()
            )
            val isPomodoroTimerValid = isValid(
                binding.pomodoroTimer, binding.editTimer.text.toString()
            )
            if (!isTaskNameValid || !isPomodoroTimerValid) {
                Toast.makeText(this, "These fields are required!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.editTask.requestFocus()
    }

    @SuppressLint("SetTextI18n")
    fun isValid(textView: TextView, content: String): Boolean {

        return if (content.isEmpty()) {
            textView.setTextColor(Color.RED)
            textView.setTypeface(textView.typeface, Typeface.BOLD)
            if (!textView.text.contains("*")) {
                textView.text = "${textView.text} *"
            }
            false
        } else {
            textView.setTextColor(Color.BLACK)
            textView.setTypeface(textView.typeface, Typeface.NORMAL)
            textView.text = textView.text.toString().removeSuffix("*")
            true
        }
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
            if (DateHelper.checkTimeIsValid(picker.hour, picker.minute)) {
                binding.editTimer.setText(" ${picker.hour} : ${picker.minute}")
            } else {
                Toast.makeText(this, "Select a valid time between 0 and 1 hour", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}
