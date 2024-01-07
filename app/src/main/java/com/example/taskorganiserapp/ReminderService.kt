package com.example.taskorganiserapp

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.taskorganiserapp.databinding.ReminderDialogBinding
import com.example.taskorganiserapp.databinding.TaskCreatorBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ReminderService(private val context: Context, private val binding: TaskCreatorBinding) {

    private lateinit var numberPicker: NumberPicker
    private lateinit var unitsPicker: NumberPicker
    private lateinit var pendingIntent: PendingIntent
    private lateinit var alarmManager: AlarmManager
    private val reminderDialogBinding = ReminderDialogBinding.inflate(LayoutInflater.from(context))
    private var isAlarm: Boolean = false
    private var reminderOnTime: Boolean = true
    private var date: LocalDate? = null
    private var time: LocalTime? = null
    private var dateBuffer: LocalDate? = null
    private var timeBuffer: LocalTime? = null
    
    fun createNotificationChannel() {
        val name = "Notification Channel"
        val desc = "Some Description"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.createNotificationChannel(channel)
    }

    fun reminderCreator(selectedDate: LocalDate, selectedTime: LocalTime): LocalDateTime? {
        val reminderDialog = AlertDialog.Builder(context)
        val timeUnits = context.resources.getStringArray(R.array.timeUnits)

        reminderDialogBinding.reminderValue.text = LocalDateTime.of(selectedDate, selectedTime).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        numberPicker = reminderDialogBinding.numberPicker
        unitsPicker = reminderDialogBinding.unitsPicker
        date = selectedDate
        time = selectedTime
        dateBuffer = selectedDate
        timeBuffer = selectedTime

        reminderDialogBinding.notification.setOnClickListener {
            isAlarm = false
        }
        reminderDialogBinding.alarm.setOnClickListener {
            isAlarm = true
        }
        reminderDialogBinding.beforeTime.setOnClickListener {
            reminderDialogBinding.pickers.visibility = View.VISIBLE
            reminderOnTime = false
        }
        reminderDialogBinding.onTime.setOnClickListener {
            reminderDialogBinding.pickers.visibility = View.GONE
            reminderOnTime = true
        }

        numberPicker.minValue = 0
        numberPicker.maxValue = 999

        unitsPicker.minValue = 0
        unitsPicker.maxValue = 5
        unitsPicker.displayedValues = timeUnits

        numberPicker.setOnValueChangedListener { _, _, _ ->
            updateDisplayedDateTime()
        }
        unitsPicker.setOnValueChangedListener { _, _, _ ->
            updateDisplayedDateTime()
        }

        with(reminderDialog) {
            setPositiveButton("OK") { _, _ ->
                if (!reminderOnTime) {
                    when (unitsPicker.value) {
                        0 -> time = time!!.minusMinutes(numberPicker.value.toLong())
                        1 -> time = time!!.minusHours(numberPicker.value.toLong())
                        2 -> date = date!!.minusDays(numberPicker.value.toLong())
                        3 -> date = date!!.minusWeeks(numberPicker.value.toLong())
                        4 -> date = date!!.minusMonths(numberPicker.value.toLong())
                        5 -> date = date!!.minusYears(numberPicker.value.toLong())
                    }
                }
                binding.deleteReminder.visibility = View.VISIBLE
                binding.leftMarginOfReminder.visibility = View.VISIBLE
                setReminder()
            }
            setNegativeButton("Cancel") { _, _ -> }
            setView(reminderDialogBinding.root)
            show()
        }
        return LocalDateTime.of(date, time)
    }

    private fun updateDisplayedDateTime() {
        if(numberPicker.value != 0)
            when (unitsPicker.value) {
                0 -> timeBuffer = time!!.minusMinutes(numberPicker.value.toLong())
                1 -> timeBuffer = time!!.minusHours(numberPicker.value.toLong())
                2 -> dateBuffer = date!!.minusDays(numberPicker.value.toLong())
                3 -> dateBuffer = date!!.minusWeeks(numberPicker.value.toLong())
                4 -> dateBuffer = date!!.minusMonths(numberPicker.value.toLong())
                5 -> dateBuffer = date!!.minusYears(numberPicker.value.toLong())
            }
        val dateTime = dateBuffer!!.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " " + timeBuffer!!.format(DateTimeFormatter.ofPattern("HH:mm"))
        reminderDialogBinding.reminderValue.text = dateTime
    }

    private fun setReminder() {
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java)
        intent.putExtra(titleExtra, "Reminder")
        intent.putExtra(messageExtra, binding.name.text.toString())
        intent.putExtra(reminderType, isAlarm)

        pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager = (context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager)!!
        val localDateTime = LocalDateTime.of(date, time)
        val timeInMillis = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
        Toast.makeText(context, "Alarm set on: $localDateTime", Toast.LENGTH_SHORT).show()
        binding.setReminderButton.text = localDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    }

    fun cancelReminder() {
        alarmManager.cancel(pendingIntent)
        Toast.makeText(context, "Alarm canceled!", Toast.LENGTH_SHORT).show()
    }
}