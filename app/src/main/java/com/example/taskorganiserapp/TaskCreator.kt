package com.example.taskorganiserapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.marginLeft
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskorganiserapp.databinding.TaskCreatorBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
    private var subtaskCounter = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = TaskCreatorBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()

        binding.name.requestFocus()

        binding.name.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.name.windowToken, 0)
                return@setOnEditorActionListener true
            }
            false
        }

        dialog?.setOnShowListener {
            val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.isDraggable = false
            }
        }

        subtaskViewModel = ViewModelProvider(activity)[SubtaskViewModel::class.java]
        reminderService = ReminderService(activity, binding)
        reminderService.createNotificationChannel()
        prepareCreator()

        subtaskViewModel.subtaskItems.observe(this) {
            binding.subtasks.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = SubtaskItemAdapter(it!!, this@TaskCreator)
            }
        }
        binding.name.setOnClickListener {
            binding.nameLayout.error = null
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
            openReminderDialog()
        }
        binding.deleteDate.setOnClickListener {
            date = null
            updateDateButtonText()
            binding.deleteDate.visibility = View.GONE
            binding.leftMarginOfDate.visibility = View.GONE
        }
        binding.deleteTime.setOnClickListener {
            time = null
            updateTimeButtonText()
            binding.deleteTime.visibility = View.GONE
            binding.leftMarginOfTime.visibility = View.GONE
        }
        binding.deleteReminder.setOnClickListener {
            reminderService.cancelReminder()
            binding.deleteReminder.visibility = View.GONE
            binding.leftMarginOfReminder.visibility = View.GONE
        }
    }

    private fun addSubtask() {
        val name = binding.subtaskName.text.toString()
        subtaskViewModel.addSubtaskItem(SubtaskItem(id = subtaskCounter, name = name, completed = false))
        subtaskCounter++

        if(subtaskCounter > 0)
            binding.scrollView.visibility = View.VISIBLE

        if(subtaskCounter < 4)
            binding.scrollView.layoutParams.height += 100

        binding.subtasks.scrollToPosition(subtaskCounter)
        binding.scrollView.post {
            binding.scrollView.fullScroll(View.FOCUS_DOWN)
        }
        binding.subtaskName.requestFocus()
    }

    @SuppressLint("SetTextI18n")
    private fun prepareCreator() {
        if(task != null) {
            binding.title.text = "Edit Task"
            binding.name.setText(task!!.name)
            binding.note.setText(task!!.note)

            if(task!!.time != null) {
                time = task!!.time
                updateTimeButtonText()
            }

            if(task!!.date != null) {
                date = task!!.date
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
            binding.deleteDate.visibility = View.VISIBLE
            binding.leftMarginOfDate.visibility = View.VISIBLE
        }
    }

    private fun updateDateButtonText() {
        if(date != null)
            binding.datePickerButton.text = String.format("%02d/%02d/%04d", date!!.dayOfMonth, date!!.monthValue, date!!.year)
        else
            binding.datePickerButton.text = "SELECT DATE"
    }

    private fun openTimePicker() {
        if(time == null)
            time = LocalTime.now()

        val listener = TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
            time = LocalTime.of(selectedHour, selectedMinute)
            updateTimeButtonText()
            binding.deleteTime.visibility = View.VISIBLE
            binding.leftMarginOfTime.visibility = View.VISIBLE
        }

        val timePicker = TimePickerDialog(activity, listener, time!!.hour, time!!.minute, true)
        timePicker.setTitle("Set Time")
        timePicker.show()
    }

    private fun updateTimeButtonText() {
        if(time != null)
            binding.timePickerButton.text = String.format("%02d:%02d", time!!.hour, time!!.minute)
        else
            binding.timePickerButton.text = "SELECT TIME"
    }

    private fun openReminderDialog() {
        if(date != null) {
            if (time == null)
                reminderService.reminderCreator(date!!, LocalTime.of(0, 0))
            else
                reminderService.reminderCreator(date!!, time!!)
        }
        else
            Toast.makeText(context, "Deadline is not set!", Toast.LENGTH_SHORT).show()
    }

    private fun saveTask()
    {
        if(binding.name.text.isNullOrEmpty())
            binding.nameLayout.error = "Name is required!"
        else {
            val name = binding.name.text.toString()
            val note = binding.note.text.toString()
            var priority = 1 // default setting

            if (binding.high.isChecked)
                priority = 2
            else if (binding.veryHigh.isChecked)
                priority = 3

            if (task == null) {
                SubtaskItem.creatorMode = false
                val newTask = TaskItem(
                    listID = listID,
                    name = name,
                    note = note,
                    time = time,
                    date = date,
                    priority = priority,
                    completed = false,
                    subtasks = subtaskViewModel.subtaskItems.value?.toList()
                )

                taskViewModel.addTaskItem(newTask)

                Log.i("subtask", "task id=" + taskViewModel.lastInsertedID)
            } else {
                task!!.name = name
                task!!.note = note
                task!!.time = time
                task!!.date = date
                task!!.priority = priority

                task!!.subtasks = subtaskViewModel.subtaskItems.value?.toList()

                taskViewModel.updateTaskItem(task!!)
            }
            SubtaskItem.creatorMode = false
            dismiss()
        }
    }

    override fun deleteSubtaskItem(subtask: SubtaskItem) {
        subtaskViewModel.deleteSubtaskItem(subtask)
        subtaskCounter--

        if(subtaskCounter == 0)
            binding.scrollView.visibility = View.GONE

        if(subtaskCounter in 0..2)
            binding.scrollView.layoutParams.height -= 100
    }

    override fun setCompleteSubtaskItem(subtask: SubtaskItem) {
        subtaskViewModel.setState(subtask, true)
    }

    override fun setIncompleteSubtaskItem(subtask: SubtaskItem) {
        subtaskViewModel.setState(subtask, false)
    }
}