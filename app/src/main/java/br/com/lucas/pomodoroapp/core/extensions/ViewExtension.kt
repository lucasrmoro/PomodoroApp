package br.com.lucas.pomodoroapp.core.extensions

import android.view.View

fun View.display(display: Boolean, gone: Boolean = true){
    this.visibility = when{
        display -> View.VISIBLE
        display.not() && gone -> View.GONE
        else -> View.INVISIBLE
    }
}