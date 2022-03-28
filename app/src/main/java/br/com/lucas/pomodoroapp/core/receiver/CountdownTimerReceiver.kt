package br.com.lucas.pomodoroapp.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.lucas.pomodoroapp.helpers.PreferencesHelper

class CountdownTimerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.getSharedPreferences(PreferencesHelper.MY_PREFS, Context.MODE_PRIVATE).edit()
            .remove(PreferencesHelper.ACTIVE_POMODORO_TIMER).apply()

        val countdownFinishIntent = Intent(COUNTDOWN_TIME_STEP_FINISH_ACTION)
        LocalBroadcastManager.getInstance(context).sendBroadcast(countdownFinishIntent)
    }

    companion object {
        const val COUNTDOWN_TIMER_FINISH = "Countdown timer finish intent action"
        const val COUNTDOWN_TIME_STEP_FINISH_ACTION = "Countdown time step finish intent action"
    }
}