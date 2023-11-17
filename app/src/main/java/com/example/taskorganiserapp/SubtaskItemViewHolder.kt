package com.example.taskorganiserapp

import android.content.Context
import android.graphics.Paint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.taskorganiserapp.databinding.SubtaskItemBinding
import com.example.taskorganiserapp.databinding.TaskItemBinding

class SubtaskItemViewHolder (
    private val context: Context,
    private val binding: SubtaskItemBinding,
    private val clickListener: SubtaskItemClickListener
): RecyclerView.ViewHolder(binding.root) {

    fun bindSubtaskItem(subtask: SubtaskItem) {
        binding.subtaskName.text = subtask.name

        if(subtask.completed)
            binding.subtaskName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        else
            binding.subtaskName.paintFlags = binding.subtaskName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

        binding.subtaskCheckBox.setImageResource(subtask.setStateImage())

        binding.subtaskCheckBox.setOnClickListener {
            if (!subtask.completed)
                clickListener.setCompleteSubtaskItem(subtask)
            else
                clickListener.setIncompleteSubtaskItem(subtask)
        }

        if(subtask.creatorMode) {
            binding.deleteSubtask.visibility = View.VISIBLE
            binding.deleteSubtask.setOnClickListener {
                clickListener.deleteSubtaskItem(subtask)
            }
        }
        else
            binding.deleteSubtask.visibility = View.GONE
    }
}