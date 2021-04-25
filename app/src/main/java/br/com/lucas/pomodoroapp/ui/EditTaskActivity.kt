package br.com.lucas.pomodoroapp.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IntRange
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import br.com.lucas.pomodoroapp.databinding.ActivityEditTaskBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.Builder
import com.google.android.material.timepicker.TimeFormat
import java.util.*

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
            makeRequiredText(binding.taskName)
            makeRequiredText(binding.pomodoroTimer)
            Toast.makeText(this, "Task \"$taskName\" saved", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.editTask.requestFocus()
    }

    @SuppressLint("SetTextI18n")
    fun makeRequiredText(textView: TextView) {
        textView.setTextColor(Color.RED)
        textView.setTypeface(textView.typeface, Typeface.BOLD)
        textView.text = "${textView.text} *"
    }


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
                Toast.makeText(this, "Select a valid time between 0 and 1 hour", Toast.LENGTH_LONG).show()
            }
        }
    }
}