package com.example.taskorganiserapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskorganiserapp.databinding.SubtaskItemBinding
import com.example.taskorganiserapp.databinding.TaskItemBinding

class SubtaskItemAdapter(
    private val subtaskItems: List<SubtaskItem>,
    private val clickListener: SubtaskItemClickListener
): RecyclerView.Adapter<SubtaskItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtaskItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SubtaskItemBinding.inflate(inflater, parent, false)
        return SubtaskItemViewHolder(parent.context, binding, clickListener)
    }

    override fun onBindViewHolder(holder: SubtaskItemViewHolder, position: Int) {
        holder.bindSubtaskItem(subtaskItems[position])
    }

    override fun getItemCount(): Int = subtaskItems.size
}