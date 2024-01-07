package com.example.taskorganiserapp

import android.app.Application
import android.content.Context

class TaskOrganiserApp: Application() {
    companion object {
        lateinit var appContext: Context
    }
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val preferences by lazy { SharedPreferencesManager(appContext) }
    val repository by lazy { TasksRepository(database.taskListDAO(), database.taskItemDAO(), preferences) }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}