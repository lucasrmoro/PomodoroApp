package br.com.lucas.pomodoroapp.core.extensions

import br.com.lucas.pomodoroapp.database.model.Task
import br.com.lucas.pomodoroapp.ui.listTaskScreen.AdapterItem

fun Task.toAdapterItem(): AdapterItem = AdapterItem(uid, taskName, pomodoroDurations)

fun AdapterItem.toTaskItem(): Task = Task(uid, taskName, pomodoroDurations)

fun List<Task>.toAdapterItems(): List<AdapterItem> = map { it.toAdapterItem() }