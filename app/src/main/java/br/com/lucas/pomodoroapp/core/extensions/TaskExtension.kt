package br.com.lucas.pomodoroapp.core.extensions

import br.com.lucas.pomodoroapp.database.Task
import br.com.lucas.pomodoroapp.ui.listTaskScreen.AdapterItem

fun Task.toAdapterItem(): AdapterItem = AdapterItem(uid, taskName, taskMinutes)

fun AdapterItem.toTaskItem(): Task = Task(uid, taskName, taskMinutes)

fun List<Task>.toAdapterItems(): List<AdapterItem> = map { it.toAdapterItem() }