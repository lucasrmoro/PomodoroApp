package br.com.lucas.pomodoroapp.ui.listTaskScreen

import androidx.annotation.ColorRes

data class AdapterItem(
    val uid: Int,
    val taskName: String,
    val taskMinutes: Int,
    val selectionState: SelectionState = SelectionState.DEFAULT,
    val switchState: SwitchState = SwitchState.DEFAULT,
    val isSwitchVisible: Boolean = true,
) {
    fun toggleSelectionState() = copy(selectionState =
    if (selectionState != SelectionState.SELECTED) SelectionState.SELECTED else SelectionState.DEFAULT)
}

enum class SelectionState(
    val scale: Float,
    @ColorRes val backgroundColor: Int,
) {
    DEFAULT(
        scale = 0f,
        backgroundColor = android.R.attr.colorBackground
    ),
    SELECTED(
        scale = 1f,
        backgroundColor = android.R.attr.colorControlHighlight
    )
}

enum class SwitchState(
    val isEnabled: Boolean,
    val isAvailable: Boolean,
) {
    DEFAULT(
        isEnabled = false,
        isAvailable = true
    ),
    ENABLED(
        isEnabled = true,
        isAvailable = true
    ),
    DISABLED(
        isEnabled = false,
        isAvailable = false
    ),
}