package br.com.lucas.pomodoroapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.lucas.pomodoroapp.databinding.ActivityEditTaskBinding
import com.google.android.material.timepicker.MaterialTimePicker
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
            val taskName = binding.editTask.text.toString()
            Toast.makeText(this, "Task \"$taskName\" saved", Toast.LENGTH_LONG).show()
        }
    }

    private fun showTimePicker() {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(10)
            .setTitleText("Select Appointment time")
            .build()
        picker.show(supportFragmentManager, "Test")
        picker.addOnPositiveButtonClickListener {
            binding.editTimer.setText(" ${picker.hour} : ${picker.minute}")
        }
    }
}