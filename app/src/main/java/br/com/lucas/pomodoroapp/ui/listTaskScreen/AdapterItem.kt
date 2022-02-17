package br.com.lucas.pomodoroapp.ui.listTaskScreen

data class AdapterItem(
    val uid: Int,
    val taskName: String,
    val taskMinutes: Int,
    private var isSelected: Boolean = false
) {
    val isTaskSelected: Boolean
        get() = isSelected

    fun toggleTask() {
        isSelected = !isSelected
    }

    fun resetTaskSelection() {
        isSelected = false
    }
}