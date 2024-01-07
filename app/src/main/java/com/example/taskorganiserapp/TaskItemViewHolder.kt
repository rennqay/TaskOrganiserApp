package com.example.taskorganiserapp

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskorganiserapp.databinding.TaskItemBinding
import java.time.format.DateTimeFormatter

class TaskItemViewHolder(
    private val context: Context,
    private val binding: TaskItemBinding,
    private val clickListener: TaskItemClickListener,
    private val subtaskItemClickListener: SubtaskItemClickListener
): RecyclerView.ViewHolder(binding.root)
{

    private val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
    private val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun bindTaskItem(task: TaskItem) {

        binding.taskName.text = task.name
        binding.taskNote.text = task.note

        when(task.priority) {
            1 -> binding.priorityIcon.visibility = View.GONE
            2 -> binding.priorityIcon.setColorFilter(Color.YELLOW)
            3 -> binding.priorityIcon.setColorFilter(Color.RED)
        }

        if(task.isCompleted) {
            val completionTimeText = "Completed on: ${task.convertCompletionTimeToString()}"
            binding.taskContainter.alpha = 0.5F
            binding.taskName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.taskNote.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.taskDate.text = completionTimeText
            binding.taskTime.visibility = View.GONE
        }
        else {
            binding.taskContainter.alpha = 1F
            binding.taskName.paintFlags = binding.taskName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            binding.taskNote.paintFlags = binding.taskNote.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

            if(task.time != null) {
                binding.taskTime.visibility = View.VISIBLE
                binding.taskTime.text = timeFormat.format(task.time)
            }
            else
                binding.taskTime.visibility = View.GONE

            if(task.date != null) {
                binding.taskDate.visibility = View.VISIBLE
                binding.taskDate.text = dateFormat.format(task.date)
            }
            else
                binding.taskDate.visibility = View.GONE

            if(task.reminderTime != null)
                binding.reminderIcon.visibility = View.VISIBLE
            else
                binding.reminderIcon.visibility = View.GONE
        }

        if(!task.subtasks.isNullOrEmpty())
            binding.subtasks.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = SubtaskItemAdapter(task.subtasks!!, subtaskItemClickListener)
            }

        binding.taskCheckBox.setImageResource(task.setStateImage())

        binding.taskCheckBox.setOnClickListener {
            if (!task.isCompleted)
                clickListener.setCompleteTaskItem(task)
            else
                clickListener.setIncompleteTaskItem(task)
        }

        binding.taskContainter.setOnClickListener {
            clickListener.editTaskItem(task)
        }
    }
}