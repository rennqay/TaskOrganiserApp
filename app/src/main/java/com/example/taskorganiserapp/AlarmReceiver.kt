package com.example.taskorganiserapp

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat

const val notificationID = 1
const val channelID = "channel0"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"
const val reminderType = "reminderType"

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val intent = Intent(context.applicationContext, MarkAsDoneReceiver::class.java)
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent.getStringExtra("titleExtra"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationID, notification)
    }
}