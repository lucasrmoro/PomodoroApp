package br.com.lucas.pomodoroapp.ui

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import br.com.lucas.pomodoroapp.R
import br.com.lucas.pomodoroapp.databinding.ActivityListTaskBinding

class ListTaskActivity : AppCompatActivity() {

    lateinit var binding: ActivityListTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { view ->
           EditTaskActivity.launch(this)
        }
    }
}