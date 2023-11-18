package com.example.taskorganiserapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskorganiserapp.databinding.TaskListItemBinding

class TaskListsAdapter(
    private val listOfTaskLists: List<TaskList>,
    private val clickListener: TaskListClickListener
): RecyclerView.Adapter<TaskListsAdapter.ViewHolder>() {

    class ViewHolder(
        private val context: Context,
        private val binding: TaskListItemBinding,
        private val clickListener: TaskListClickListener

    ): RecyclerView.ViewHolder(binding.root) {
        fun bindTaskListItem(taskList: TaskList) {
            binding.taskListName.text = taskList.name
            binding.root.setOnClickListener{
                clickListener.setTaskList(taskList)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TaskListItemBinding.inflate(inflater, parent, false)
        return ViewHolder(parent.context, binding, clickListener)
    }

    override fun getItemCount(): Int = listOfTaskLists.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTaskListItem(listOfTaskLists[position])
    }
}

interface TaskListClickListener {
    fun setTaskList(taskList: TaskList)
}
