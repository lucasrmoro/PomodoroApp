package br.com.lucas.pomodoroapp.core.extensions

import android.view.View

fun View.display(display: Boolean, gone: Boolean = true){
    if(display){
        this.visibility = View.VISIBLE
    } else {
        if(gone) this.visibility = View.GONE else this.visibility = View.INVISIBLE
    }
}