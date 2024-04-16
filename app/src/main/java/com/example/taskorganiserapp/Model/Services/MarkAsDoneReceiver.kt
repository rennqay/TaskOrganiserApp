package com.example.taskorganiserapp.Model.Services

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.taskorganiserapp.Model.TaskOrganiserApp
import ulid.ULID

class MarkAsDoneReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)

        val inputData = Data.Builder()
            .putString("ID", intent.getStringExtra("ID"))
            .build()

        val markAsDoneRequest = OneTimeWorkRequestBuilder<MarkAsDoneWorker>()
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueue(markAsDoneRequest)
        Log.i("MarkAsDoneReceiver", "received")
    }
}

class MarkAsDoneWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val taskID = inputData.getString("ID")

        taskID?.let {
            try {
                val repository = (applicationContext as TaskOrganiserApp).repository
                repository.updateTaskItemStatusByID(ULID.parseULID(taskID), true)

                return Result.success()
            } catch (exception: Exception) {
                return Result.failure()
            }
        }
        return Result.failure()
    }
}