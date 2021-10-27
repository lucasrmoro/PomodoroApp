package br.com.lucas.pomodoroapp.core.extensions

import android.view.animation.Animation
import androidx.annotation.DrawableRes
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

fun FloatingActionButton.toggleFabImage(
    clicked: Boolean,
    @DrawableRes defaultImg: Int,
    @DrawableRes newImg: Int
) {
    if (clicked)
        this.setImageResource(defaultImg)
    else
        this.setImageResource(newImg)
}