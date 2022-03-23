package br.com.lucas.pomodoroapp.core.extensions

import android.view.View
import android.widget.TextView
import com.google.android.material.slider.Slider

fun View.display(display: Boolean, gone: Boolean = true){
    this.visibility = when{
        display -> View.VISIBLE
        display.not() && gone -> View.GONE
        else -> View.INVISIBLE
    }
}

fun Int.putInTheRespectiveViews(slider: Slider, textView: TextView){
    slider.value = this.toFloat()
    textView.text = this.toString()
}