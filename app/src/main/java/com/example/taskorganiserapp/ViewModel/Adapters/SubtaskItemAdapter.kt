package com.example.taskorganiserapp.ViewModel.Adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskorganiserapp.Model.Entities.SubtaskItem
import com.example.taskorganiserapp.databinding.SubtaskItemBinding

class SubtaskItemAdapter(
    private val subtaskItems: List<SubtaskItem>,
    private val clickListener: SubtaskItemClickListener
): RecyclerView.Adapter<SubtaskItemAdapter.ViewHolder>() {

    class ViewHolder(
        private val context: Context,
        private val binding: SubtaskItemBinding,
        private val clickListener: SubtaskItemClickListener,
    ): RecyclerView.ViewHolder(binding.root) {

        fun bindSubtaskItem(subtask: SubtaskItem) {
            binding.subtaskName.text = subtask.name

            binding.subtaskCheckBox.setImageResource(subtask.setStateImage())

            binding.subtaskCheckBox.setOnClickListener {
                if (!subtask.completed) {
                    clickListener.setCompleteSubtaskItem(subtask)
                    binding.subtaskName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
                else {
                    clickListener.setIncompleteSubtaskItem(subtask)
                    binding.subtaskName.paintFlags = binding.subtaskName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
                binding.subtaskCheckBox.setImageResource(subtask.setStateImage())
            }

            if(SubtaskItem.creatorMode) {
                binding.deleteSubtask.visibility = View.VISIBLE
                binding.deleteSubtask.setOnClickListener {
                    clickListener.deleteSubtaskItem(subtask)
                }
            }
            else
                binding.deleteSubtask.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SubtaskItemBinding.inflate(inflater, parent, false)
        return ViewHolder(parent.context, binding, clickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindSubtaskItem(subtaskItems[position])
    }

    override fun getItemCount(): Int = subtaskItems.size
}

interface SubtaskItemClickListener {
    fun deleteSubtaskItem(subtask: SubtaskItem)
    fun setCompleteSubtaskItem(subtask: SubtaskItem)
    fun setIncompleteSubtaskItem(subtask: SubtaskItem)
}