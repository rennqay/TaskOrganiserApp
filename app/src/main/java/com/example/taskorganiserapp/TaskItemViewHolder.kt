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

        if(task.time != null)
            binding.taskTime.text = task.time
//            binding.taskTime.text = timeFormat.format(task.time)
        else
            binding.taskTime.text = ""

        if(task.date != null)
            binding.taskDate.text = task.date
//            binding.taskDate.text = dateFormat.format(task.date)
        else
            binding.taskDate.text = ""

        when(task.priority) {
            1 -> binding.priority.visibility = View.GONE
            2 -> binding.priority.setColorFilter(Color.YELLOW)
            3 -> binding.priority.setColorFilter(Color.RED)
        }

        if(task.completed) {
            binding.taskName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.taskNote.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.taskTime.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.taskDate.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }
        else {
            binding.taskName.paintFlags = binding.taskName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            binding.taskNote.paintFlags = binding.taskNote.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            binding.taskTime.paintFlags = binding.taskTime.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            binding.taskDate.paintFlags = binding.taskDate.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        if(!task.subtasks.isNullOrEmpty())
            binding.subtasks.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = SubtaskItemAdapter(task.subtasks!!, subtaskItemClickListener)
            }

        binding.taskCheckBox.setImageResource(task.setStateImage())

        binding.taskCheckBox.setOnClickListener {
            if (!task.completed)
                clickListener.setCompleteTaskItem(task)
            else
                clickListener.setIncompleteTaskItem(task)
        }

        binding.taskContainter.setOnClickListener {
            clickListener.editTaskItem(task)
        }
    }
}