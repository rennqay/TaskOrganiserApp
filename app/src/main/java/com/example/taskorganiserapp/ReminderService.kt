package com.example.taskorganiserapp

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.taskorganiserapp.databinding.ReminderDialogBinding
import com.example.taskorganiserapp.databinding.TaskCreatorBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

const val NOTIFICATION = true
const val ALARM = false

class ReminderService(private val context: Context) {

    private lateinit var binding: TaskCreatorBinding
    private var timeOffset: Long = 0
    private var reminderType: Boolean = NOTIFICATION // default setting
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

    fun reminderCreator(selectedDate: LocalDate, selectedTime: LocalTime?) {
        val reminderDialog = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(R.layout.reminder_dialog, null)

        val numberPicker = dialogLayout.findViewById<NumberPicker>(R.id.numberPicker)
        val unitsPicker = dialogLayout.findViewById<NumberPicker>(R.id.unitsPicker)
        val timeUnits = context.resources.getStringArray(R.array.timeUnits)

        numberPicker.minValue = 0
        numberPicker.maxValue = 999

        unitsPicker.minValue = 0
        unitsPicker.maxValue = 5
        unitsPicker.displayedValues = timeUnits

        with(reminderDialog) {
            setTitle("Set Reminder")
            setPositiveButton("OK") { _, _ ->
                if(dialogLayout.findViewById<Button>(R.id.alarm).isSelected)
                    reminderType = ALARM

                when(unitsPicker.value) {
                    0 -> timeOffset += numberPicker.value * 1000 * 60 // minutes
                    1 -> timeOffset += numberPicker.value * 1000 * 60 * 60 // hours
                    2 -> timeOffset += numberPicker.value * 1000 * 60 * 60 * 24 // days
                    3 -> timeOffset += numberPicker.value * 1000 * 60 * 60 * 24 * 7 // weeks
                    4 -> timeOffset += numberPicker.value * 1000 * 60 * 60 * 24 * 30 // months
                    5 -> timeOffset += numberPicker.value * 1000 * 60 * 60 * 24 * 30 * 12 // years
                }
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
            timeInMillis-timeOffset,
            pendingIntent
        )
        Toast.makeText(context, "Alarm set on: ${localDateTime.toString()}", Toast.LENGTH_SHORT).show()
    }
}