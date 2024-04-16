package com.example.taskorganiserapp.Model

import android.app.Application
import android.content.Context
import com.example.taskorganiserapp.Model.Database.AppDatabase
import com.example.taskorganiserapp.Model.Database.TasksRepository
import com.example.taskorganiserapp.Model.Services.SharedPreferencesManager

class TaskOrganiserApp: Application() {
    companion object {
        lateinit var appContext: Context
        lateinit var instance: TaskOrganiserApp
    }
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val preferences by lazy { SharedPreferencesManager(appContext) }
    val repository by lazy { TasksRepository(database.taskListDAO(), database.taskItemDAO(), preferences) }


    override fun onCreate() {
        super.onCreate()
        instance = this
        appContext = applicationContext
    }
}