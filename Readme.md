<h1 align="center"> 
	🚧  Pomodoro App 🚀 Under construction...  🚧
</h1>

[![Build Status](https://app.bitrise.io/app/d380e36ddbf36dce/status.svg?token=yFUskxH_8tcmGdqCYbWe0w&branch=master)](https://app.bitrise.io/app/d380e36ddbf36dce) [![codebeat badge](https://codebeat.co/badges/09c123e9-2cc1-474f-b3c1-340c3deef826)](https://codebeat.co/projects/github-com-lucasrmoro-pomodoroapp-main)

# Content table
<!--ts-->
   * [Description](#description)
   * [Features](#features)
   * [Usage](#usage)
		* [Create a task](#create-a-task)
		* [Update a task](#update-a-task)
		* [Delete a task](#delete-a-task)
   * [Tecnologies used](#tecnologies-used)
<!--te-->

## Description
A project that aims to implement the Pomodoro technique to organize and improve individual study time.

## Features

|            Features              | Already Implemented |
| ---------------------------------| :-----------------: |
| Dark theme support               |         ✔️           |
| CRUD                             |         ✔️           |
| Delete multiple tasks            |         ✔️           |
| Countdown timer                  |         ❌          |
| Notification when timer finished |         ✔️           |
| Custom ringtone                  |         ❌          |

## Usage

### Create a task
To add a new task, only press the add button in the bottom right corner, then fill it with title and Pomodoro time.

<img src="https://github.com/lucasrmoro/PomodoroApp/blob/main/img/light-mode/create-task.gif" width="250" height="500" /> <img src="https://github.com/lucasrmoro/PomodoroApp/blob/main/img/dark-mode/create-task.gif" width="250" height="500" />

&nbsp;
### Update a task
To update the task, only press it and update.

<img src="https://github.com/lucasrmoro/PomodoroApp/blob/main/img/light-mode/update-task.gif" width="250" height="500" /> <img src="https://github.com/lucasrmoro/PomodoroApp/blob/main/img/dark-mode/update-task.gif" width="250" height="500" />

&nbsp;
### Delete a task
You can delete only one task by pressing on top of it and clicking on the delete icon on the top right corner or selecting multiple tasks doing a long press on top of it and selecting how many tasks you want.

- Deleting a single task

&nbsp;
<img src="https://github.com/lucasrmoro/PomodoroApp/blob/main/img/light-mode/delete-task.gif" width="250" height="500" /> <img src="https://github.com/lucasrmoro/PomodoroApp/blob/main/img/dark-mode/delete-task.gif" width="250" height="500" />

- Deleting multiple tasks

&nbsp;
<img src="https://github.com/lucasrmoro/PomodoroApp/blob/main/img/light-mode/delete-multiple-tasks.gif" width="250" height="500" /> <img src="https://github.com/lucasrmoro/PomodoroApp/blob/main/img/dark-mode/delete-multiple-tasks.gif" width="250" height="500" />

## Tecnologies used

- Kotlin
- MVVM Architecture
- Dependency Injection (Dagger-Hilt)
- Jetpack components
- Bitrise and Firebase to CI/CD
