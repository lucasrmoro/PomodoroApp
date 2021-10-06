package br.com.lucas.pomodoroapp.core.extensions

import androidx.annotation.DrawableRes
import com.google.android.material.floatingactionbutton.FloatingActionButton

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