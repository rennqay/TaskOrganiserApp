package com.example.taskorganiserapp.View

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.taskorganiserapp.Model.Services.AlarmReceiver
import com.example.taskorganiserapp.Model.Services.channelID
import com.example.taskorganiserapp.Model.Services.messageExtra
import com.example.taskorganiserapp.Model.Services.notificationID
import com.example.taskorganiserapp.Model.Services.reminderType
import com.example.taskorganiserapp.Model.Services.titleExtra
import com.example.taskorganiserapp.R
import com.example.taskorganiserapp.databinding.ReminderDialogBinding
import com.example.taskorganiserapp.databinding.RepeatabilityDialogBinding
import ulid.ULID
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class RepeatabilityDialog(private val context: Context) {
    private lateinit var numberPicker: NumberPicker
    private lateinit var unitsPicker: NumberPicker
    private val binding = RepeatabilityDialogBinding.inflate(LayoutInflater.from(context))

    fun repeatabilityCreator(): List<Int> {
        val reminderDialog = AlertDialog.Builder(context)
        val timeUnits = context.resources.getStringArray(R.array.timeUnits)
        var repeatArray = listOf(0,0)

        binding.repeatabilityValue.text = ""
        numberPicker = binding.numberPicker
        unitsPicker = binding.unitsPicker

        numberPicker.minValue = 1
        numberPicker.maxValue = 999

        unitsPicker.displayedValues = timeUnits
        unitsPicker.minValue = 0
        unitsPicker.maxValue = 5

        numberPicker.setOnValueChangedListener { _, _, _ ->
            updateDisplayedRepeatability()
        }
        unitsPicker.setOnValueChangedListener { _, _, _ ->
            updateDisplayedRepeatability()
        }

        val parent = binding.root.parent as? ViewGroup
        parent?.removeView(binding.root)

        reminderDialog.setPositiveButton("OK") { _, _ ->
            repeatArray = listOf(unitsPicker.value, numberPicker.value)
        }
        reminderDialog.setNegativeButton("Cancel") { _, _ -> }
        reminderDialog.setView(binding.root)
        reminderDialog.show()

        return repeatArray
    }

    private fun updateDisplayedRepeatability() {
        val repeatValueInText = formatRepeatValue(binding.repeatValue.text.toString())
        var timeIntervalValueInText = ""

        when (unitsPicker.value) {
            2 -> timeIntervalValueInText = formatDaysByValue(numberPicker.value)
            3 -> timeIntervalValueInText = formatWeeksByValue(numberPicker.value)
            4 -> timeIntervalValueInText = formatMonthsByValue(numberPicker.value)
            5 -> timeIntervalValueInText = formatYearsByValue(numberPicker.value)
        }

        val repeatabilityText = "$timeIntervalValueInText przez $repeatValueInText"
        binding.repeatabilityValue.text = repeatabilityText
    }

    private fun formatRepeatValue(value: String): String {
        val text = when (value) {
            "1" -> "1 raz"
            else -> "$value razy"
        }
        return text
    }

    private fun formatDaysByValue(value: Int): String {
        val text = when (value) {
            1 -> "codzennie"
            else -> "co $value dni"
        }
        return text
    }

    private fun formatWeeksByValue(value: Int): String {
        val text = when (value) {
            1 -> "co tydzień"
            else -> "co $value tygodnie"
        }
        return text
    }

    private fun formatMonthsByValue(value: Int): String {
        val text = when (value) {
            1 -> "co miesiąc"
            else -> "co $value miesiące"
        }
        return text
    }

    private fun formatYearsByValue(value: Int): String {
        val text = when (value) {
            1 -> "co rok"
            else -> "co $value lata"
        }
        return text
    }
}
