package br.com.lucas.pomodoroapp.core.extensions

import android.widget.CompoundButton
import androidx.appcompat.widget.SwitchCompat

fun SwitchCompat.isNotCheckedByHuman(
    value: Boolean,
    listener: CompoundButton.OnCheckedChangeListener,
) {
    setOnCheckedChangeListener(null)
    isChecked = value
    setOnCheckedChangeListener(listener)
}