package com.example.taskorganiserapp.ViewModel.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskorganiserapp.Model.Entities.TaskItem
import com.example.taskorganiserapp.databinding.TaskItemBinding

class TaskItemAdapter(
    private val taskItems: List<TaskItem>,
    private val clickListener: TaskItemClickListener,
    private val subtaskItemClickListener: SubtaskItemClickListener
): RecyclerView.Adapter<TaskItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TaskItemBinding.inflate(inflater, parent, false)
        return TaskItemViewHolder(parent.context, binding, clickListener, subtaskItemClickListener)
    }

    override fun onBindViewHolder(holder: TaskItemViewHolder, position: Int) {
        holder.bindTaskItem(taskItems[position])
    }

    override fun getItemCount(): Int = taskItems.size
}



interface TaskItemClickListener {
    fun editTaskItem(task: TaskItem)
    fun setCompleteTaskItem(task: TaskItem)
    fun setIncompleteTaskItem(task: TaskItem)
}