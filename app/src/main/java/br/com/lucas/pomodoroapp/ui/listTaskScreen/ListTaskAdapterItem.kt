package br.com.lucas.pomodoroapp.ui.listTaskScreen


data class ListTaskAdapterItem(
    val uid: Int,
    val taskName: String,
    val taskMinutes: Int,
    var isSelected: Boolean = false,
    var isTimerSwitchViewVisible: Boolean = true
){
    fun isTaskSelected(): Boolean{
        return isSelected
    }

    fun toggleTask(){
        isSelected = !isSelected
    }

    fun resetTaskSelection(){
        isSelected = false
    }
}