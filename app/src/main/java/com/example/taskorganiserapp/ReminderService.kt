package com.example.taskorganiserapp

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.taskorganiserapp.databinding.ReminderDialogBinding
import com.example.taskorganiserapp.databinding.TaskCreatorBinding
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

const val NOTIFICATION = true
const val ALARM = false

class ReminderService(private val context: Context, private val binding: TaskCreatorBinding) {

    private var reminderType: Boolean = NOTIFICATION // default setting
    private var date: LocalDate? = null
    private var time: LocalTime? = null
    
    fun createNotificationChannel() {
        val name = "Notification Channel"
        val desc = "Some Description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.createNotificationChannel(channel)

    }

    fun reminderCreator(selectedDate: LocalDate, selectedTime: LocalTime) {
        val reminderDialog = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(R.layout.reminder_dialog, null)

        val numberPicker = dialogLayout.findViewById<NumberPicker>(R.id.numberPicker)
        val unitsPicker = dialogLayout.findViewById<NumberPicker>(R.id.unitsPicker)
        val beforeTimeButton = dialogLayout.findViewById<Button>(R.id.beforeTime)
        val onTimeButton = dialogLayout.findViewById<Button>(R.id.onTime)
        val timeUnits = context.resources.getStringArray(R.array.timeUnits)

        beforeTimeButton.setOnClickListener{
            numberPicker.visibility = View.VISIBLE
            unitsPicker.visibility = View.VISIBLE
        }

        onTimeButton.setOnClickListener{
            numberPicker.visibility = View.GONE
            unitsPicker.visibility = View.GONE
        }

        date = selectedDate
        time = selectedTime

        numberPicker.minValue = 0
        numberPicker.maxValue = 999

        unitsPicker.minValue = 0
        unitsPicker.maxValue = 5
        unitsPicker.displayedValues = timeUnits

        numberPicker.setOnValueChangedListener { _, _, _ ->
            
        }

            when (unitsPicker.value) {
                0 -> time!!.minusMinutes(numberPicker.value.toLong())
                1 -> time!!.minusHours(numberPicker.value.toLong())
                2 -> date!!.minusDays(numberPicker.value.toLong())
                3 -> date!!.minusWeeks(numberPicker.value.toLong())
                4 -> date!!.minusMonths(numberPicker.value.toLong())
                5 -> date!!.minusYears(numberPicker.value.toLong())
            }


        with(reminderDialog) {
            setTitle("Set Reminder")
            setPositiveButton("OK") { _, _ ->

                if(beforeTimeButton.isSelected) {
                    when (unitsPicker.value) {
                        0 -> time!!.minusMinutes(numberPicker.value.toLong())
                        1 -> time!!.minusHours(numberPicker.value.toLong())
                        2 -> date!!.minusDays(numberPicker.value.toLong())
                        3 -> date!!.minusWeeks(numberPicker.value.toLong())
                        4 -> date!!.minusMonths(numberPicker.value.toLong())
                        5 -> date!!.minusYears(numberPicker.value.toLong())
                    }
                }
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
        binding.setReminderButton.text = localDateTime.toString()
    }
}