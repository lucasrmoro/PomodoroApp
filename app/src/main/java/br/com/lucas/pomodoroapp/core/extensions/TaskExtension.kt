package br.com.lucas.pomodoroapp.core.extensions

import br.com.lucas.pomodoroapp.database.Task
import br.com.lucas.pomodoroapp.ui.listTaskScreen.ListTaskAdapterItem

fun Task.toAdapterItem(): ListTaskAdapterItem{
    return ListTaskAdapterItem(uid, taskName, taskMinutes)
}

fun ListTaskAdapterItem.toTaskItem(): Task{
    return Task(uid, taskName, taskMinutes)
}