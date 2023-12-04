package com.example.taskorganiserapp

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskorganiserapp.databinding.TaskCreatorBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date

class TaskCreator(private var task: TaskItem?, private val listID: Long, private val taskViewModel: TaskViewModel) : BottomSheetDialogFragment(), SubtaskItemClickListener {

    private lateinit var binding: TaskCreatorBinding
    private lateinit var subtaskViewModel: SubtaskViewModel
    private lateinit var reminderService: ReminderService
    private var time: LocalTime? = null
    private var date: LocalDate? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()

        subtaskViewModel = ViewModelProvider(activity)[SubtaskViewModel::class.java]
        reminderService = ReminderService(activity, binding)
        reminderService.createNotificationChannel()
        prepareCreator()

        subtaskViewModel.subtaskItems.observe(this) {
            binding.subtasks.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = SubtaskItemAdapter(it, this@TaskCreator)
            }
        }

        binding.addSubtask.setOnClickListener {
            addSubtask()
        }
        binding.saveButton.setOnClickListener {
            saveTask()
        }
        binding.timePickerButton.setOnClickListener {
            openTimePicker()
        }
        binding.datePickerButton.setOnClickListener {
            openDatePicker()
        }
        binding.setReminderButton.setOnClickListener {
            if(date != null) {
                if (time == null)
                    reminderService.reminderCreator(date!!, LocalTime.of(0, 0))
                else
                    reminderService.reminderCreator(date!!, time!!)
            }
            else
                Toast.makeText(context, "Deadline is not set!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addSubtask() {
        val name = binding.subtaskName.text.toString()
        subtaskViewModel.addSubtaskItem(SubtaskItem( name = name, completed = false))
    }

    @SuppressLint("SetTextI18n")
    private fun prepareCreator() {
        if(task != null) {
            binding.title.text = "Edit Task"
            binding.name.setText(task!!.name)
            binding.note.setText(task!!.note)

            if(task!!.time != null) {
                time = task!!.parseTime()
                updateTimeButtonText()
            }

            if(task!!.date != null) {
                date = task!!.parseDate()
                updateDateButtonText()
            }

            when(task!!.priority) {
                1 -> binding.normal.isChecked = true
                2 -> binding.high.isChecked = true
                3 -> binding.veryHigh.isChecked = true
            }

            if(!task!!.subtasks.isNullOrEmpty())
                subtaskViewModel.subtaskItems.value = task!!.subtasks!!.toMutableList()
        }
        else {
            binding.title.text = "Create Task"
            subtaskViewModel.subtaskItems.value?.clear()
        }
        SubtaskItem.creatorMode = true
    }

    private fun openDatePicker() {
        if(date == null)
            date = LocalDate.now()

        val activity = requireActivity()
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Set Date")
            .build()

        datePicker.show(activity.supportFragmentManager, "datePicker" )
        datePicker.addOnPositiveButtonClickListener {
            date = Date(it).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            updateDateButtonText()
        }
    }

    private fun updateDateButtonText() {
        binding.datePickerButton.text = String.format("%02d/%02d/%04d", date!!.dayOfMonth, date!!.monthValue, date!!.year)
    }

    private fun openTimePicker() {
        if(time == null)
            time = LocalTime.now()

        val listener = TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
            time = LocalTime.of(selectedHour, selectedMinute)
            updateTimeButtonText()
        }

        val timePicker = TimePickerDialog(activity, listener, time!!.hour, time!!.minute, true)
        timePicker.setTitle("Set Time")
        timePicker.show()
    }

    private fun updateTimeButtonText() {
        binding.timePickerButton.text = String.format("%02d:%02d", time!!.hour, time!!.minute)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = TaskCreatorBinding.inflate(inflater,container,false)
        return binding.root
    }

    private fun saveTask()
    {
        val name = binding.name.text.toString()
        val note = binding.note.text.toString()
        var priority = 1 // default setting

        if(binding.high.isChecked)
            priority = 2
        else if(binding.veryHigh.isChecked)
            priority = 3

        if(task == null) {
            SubtaskItem.creatorMode = false
            val newTask = TaskItem(
                listID = listID,
                name = name,
                note = note,
                time = time.toString(),
                date = date.toString(),
                priority = priority,
                completed = false)

            taskViewModel.addTaskItem(newTask)
            subtaskViewModel.setTaskIDForEachSubtask(taskViewModel.lastInsertedID)
            newTask.subtasks = subtaskViewModel.subtaskItems.value?.toList()
            taskViewModel.updateTaskItem(newTask)
        }
        else {
            taskViewModel.updateTaskItem(TaskItem(task!!.id, task!!.listID, name, note, time.toString(), date.toString(), priority, false))
        }
        SubtaskItem.creatorMode = false
        dismiss()
    }

    override fun deleteSubtaskItem(subtask: SubtaskItem) {
        subtaskViewModel.deleteSubtaskItem(subtask)
    }

    override fun setCompleteSubtaskItem(subtask: SubtaskItem) {
        subtaskViewModel.setCompleted(subtask)
    }

    override fun setIncompleteSubtaskItem(subtask: SubtaskItem) {
        subtaskViewModel.setUncompleted(subtask)
    }
}