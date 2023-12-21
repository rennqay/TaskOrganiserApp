package com.example.taskorganiserapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [TaskItem::class, TaskList::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
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