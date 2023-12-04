package com.example.taskorganiserapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TaskItem::class, TaskList::class, SubtaskItem::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun subtaskItemDAO(): SubtaskItemDAO
    abstract fun taskItemDAO(): TaskItemDAO
    abstract fun taskListDAO(): TaskListDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "storage"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}