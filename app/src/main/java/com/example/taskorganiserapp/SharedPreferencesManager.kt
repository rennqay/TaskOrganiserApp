package com.example.taskorganiserapp

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE)

    fun isFirstRun(): Boolean {
        val isFirstRun = sharedPreferences.getBoolean("firstRun", true)
        if (isFirstRun)
            sharedPreferences.edit().putBoolean("firstRun", false).apply()

        return isFirstRun
    }
}