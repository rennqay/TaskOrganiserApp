package com.example.taskorganiserapp

import android.app.Application

class TaskOrganiserApp: Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { TasksRepository(database.taskListDAO(), database.taskItemDAO(), database.subtaskItemDAO()) }
}