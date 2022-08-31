package br.com.lucas.pomodoroapp.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.lucas.pomodoroapp.helpers.PomodoroTimerHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CountdownTimerReceiver : BroadcastReceiver() {

    @Inject
    lateinit var pomodoroTimerHelper: PomodoroTimerHelper

    override fun onReceive(context: Context, intent: Intent) {
        pomodoroTimerHelper.syncPomodoroTimerSteps()

        val countdownStepFinishIntent = Intent(COUNTDOWN_TIME_STEP_FINISH_ACTION)
        LocalBroadcastManager.getInstance(context).sendBroadcast(countdownStepFinishIntent)
    }

    companion object {
        const val COUNTDOWN_BROADCAST_INTENT_ACTION = "Countdown timer intent action"
        const val COUNTDOWN_TIME_STEP_FINISH_ACTION = "Countdown time step finish intent action"
    }
}