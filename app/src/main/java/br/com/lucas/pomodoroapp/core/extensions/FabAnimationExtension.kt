package br.com.lucas.pomodoroapp.core.extensions

import android.view.animation.Animation
import com.google.android.material.floatingactionbutton.FloatingActionButton

fun FloatingActionButton.toggleFabAnimation(
    clicked: Boolean,
    openAnimation: Animation,
    closeAnimation: Animation
) {
    if (clicked)
        this.startAnimation(closeAnimation)
    else
        this.startAnimation(openAnimation)
}