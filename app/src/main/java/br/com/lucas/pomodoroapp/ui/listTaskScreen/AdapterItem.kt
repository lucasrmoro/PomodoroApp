package br.com.lucas.pomodoroapp.ui.listTaskScreen

data class AdapterItem(
    val uid: Int,
    val taskName: String,
    val taskMinutes: Int,
    var isSelected: Boolean = false
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