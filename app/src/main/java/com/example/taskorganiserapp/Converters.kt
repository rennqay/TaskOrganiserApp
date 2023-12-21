package com.example.taskorganiserapp

import androidx.room.TypeConverter
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class Converters {
    private val objectMapper: ObjectMapper = ObjectMapper()

    @TypeConverter
    fun fromStringToLocalTime(value: String?): LocalTime? {
        return if(value == "")
            null
        else
            LocalTime.parse(value, DateTimeFormatter.ofPattern("HH:mm"))
    }

    @TypeConverter
    fun fromLocalTimeToString(time: LocalTime?): String {
        return time?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: ""
    }

    @TypeConverter
    fun fromStringToLocalDate(value: String?): LocalDate? {
        return if(value == "")
            null
        else
            LocalDate.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }

    @TypeConverter
    fun fromLocalDateToString(date: LocalDate?): String {
        return date?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: ""
    }

    @TypeConverter
    fun fromStringToSubtaskList(subtaskList: String?): List<SubtaskItem> {
        return try {
            objectMapper.readValue(subtaskList, object : TypeReference<List<SubtaskItem>>() {})
        } catch (e: Exception) {
            throw RuntimeException("Error converting string to SubtaskItem list", e)
        }
    }

    @TypeConverter
    fun fromSubtaskListToString(subtaskItems: List<SubtaskItem>): String {
        return try {
            objectMapper.writeValueAsString(subtaskItems)
        } catch (e: Exception) {
            throw RuntimeException("Error converting SubtaskItem list to string", e)
        }
    }
}