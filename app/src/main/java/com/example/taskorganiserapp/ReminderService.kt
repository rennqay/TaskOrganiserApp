package com.example.taskorganiserapp

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.taskorganiserapp.databinding.ReminderDialogBinding
import com.example.taskorganiserapp.databinding.TaskCreatorBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

const val NOTIFICATION = true
const val ALARM = false
const val WHOLE_DAY = 0
const val ON_TIME = 1
const val BEFORE_TIME = 2

class ReminderService(private val context: Context) {

    private lateinit var binding: TaskCreatorBinding
    private lateinit var reminderDialogBinding: ReminderDialogBinding
    private var timeOffset: Long = 0
    private var reminderType: Boolean = NOTIFICATION // default setting
    private var reminderTimeType: Int = ON_TIME // default setting
    private val date: LocalDate? = null
    private val time: LocalTime? = null

    fun createNotificationChannel() {
        val name = "Notification Channel"
        val desc = "Some Description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.createNotificationChannel(channel)

    }

    fun reminderCreator() {
        val reminderDialog = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(R.layout.reminder_dialog, null)

        val timeValue = reminderDialogBinding.timeValue
        val timeType = reminderDialogBinding.timeType
        val timeUnits = context.resources.getStringArray(R.array.timeUnits)

        timeValue.minValue = 0
        timeValue.maxValue = 999

        timeType.minValue = 0
        timeType.maxValue = 5
        timeType.displayedValues = timeUnits

        when(timeType.value) {
            0 -> timeOffset += timeValue.value * 1000 * 60 // minutes
            1 -> timeOffset += timeValue.value * 1000 * 60 * 60 // hours
            2 -> timeOffset += timeValue.value * 1000 * 60 * 60 * 24 // days
            3 -> timeOffset += timeValue.value * 1000 * 60 * 60 * 24 * 7 // weeks
            4 -> timeOffset += timeValue.value * 1000 * 60 * 60 * 24 * 30 // months
            5 -> timeOffset += timeValue.value * 1000 * 60 * 60 * 24 * 30 * 12 // years
        }

        with(reminderDialog) {
            setTitle("Set Reminder")
            setPositiveButton("OK") { _, _ ->
                if(reminderDialogBinding.alarm.isSelected)
                    reminderType = ALARM

                if(reminderDialogBinding.beforeTime.isSelected)
                    reminderTimeType = BEFORE_TIME
                else if(reminderDialogBinding.wholeDay.isChecked)
                    reminderTimeType = WHOLE_DAY

                setReminder()
            }
            setNegativeButton("Cancel") { _, _ -> }
            setView(dialogLayout)
            show()
        }
    }

    private fun setReminder() {
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java)
        intent.putExtra(titleExtra, binding.name.text.toString())
        intent.putExtra(messageExtra, binding.note.text.toString())

        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        val localDateTime = LocalDateTime.of(date, time)
        val timeInMillis = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        alarmManager?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
        Toast.makeText(context, "Alarm set on: ${localDateTime.toString()}", Toast.LENGTH_SHORT).show()
    }
}