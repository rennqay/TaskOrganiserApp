package com.example.taskorganiserapp.Model.Services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.taskorganiserapp.R

const val notificationID = 1
const val channelID = "channel0"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"
const val reminderType = "reminderType"

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val actionIntent = Intent(context, MarkAsDoneReceiver::class.java)
        actionIntent.putExtra("ID", intent.getStringExtra("ID"))
        val actionPendingIntent = PendingIntent.getBroadcast(context, 0, actionIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val action = NotificationCompat.Action.Builder(R.drawable.confirm_button, "Wykonane", actionPendingIntent).build()

        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent.getStringExtra("titleExtra"))
            .setContentText(intent.getStringExtra("messageExtra"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .addAction(action)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationID, notification)
    }
}