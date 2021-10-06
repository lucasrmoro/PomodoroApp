package br.com.lucas.pomodoroapp.core.extensions

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes

fun loadAnim(
    context: Context,
    @AnimRes animation: Int
): Animation {
    return AnimationUtils.loadAnimation(
            context,
            animation
        )

}