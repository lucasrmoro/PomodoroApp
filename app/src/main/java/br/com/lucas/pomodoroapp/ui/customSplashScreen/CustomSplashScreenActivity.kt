package br.com.lucas.pomodoroapp.ui.customSplashScreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.lucas.pomodoroapp.databinding.ActivitySplashScreenBinding
import br.com.lucas.pomodoroapp.ui.listTaskScreen.ListTaskActivity

class CustomSplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = Intent(
            this@CustomSplashScreenActivity,
            ListTaskActivity::class.java
        )

        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.R) {
            startActivity(intent)
        } else {
            binding.pomodoroIcon.alpha = 0f
            binding.pomodoroIcon.animate().setDuration(2000).alpha(1f).withEndAction {
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        }
    }
}