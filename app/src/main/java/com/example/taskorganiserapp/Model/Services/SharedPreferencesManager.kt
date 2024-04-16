package com.example.taskorganiserapp.Model.Services

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences by lazy { context.getSharedPreferences("Preferences", Context.MODE_PRIVATE) }

    fun isFirstRun(): Boolean {
        val isFirstRun = sharedPreferences.getBoolean("firstRun", true)
        if (isFirstRun)
            sharedPreferences.edit().putBoolean("firstRun", false).apply()

        return isFirstRun
    }

    fun setSortType(value: Int) {
        sharedPreferences.edit().apply {
            putInt("sortType", value)
            apply()
        }
    }

    fun getSortType(): Int {
        return sharedPreferences.getInt("sortType", 0)
    }
}