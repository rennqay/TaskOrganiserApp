package com.example.taskorganiserapp.ViewModel.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskorganiserapp.Model.Entities.TaskList
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
            val totalTasks = taskList.quantityOfToDoTasks + taskList.quantityOfCompletedTasks
            val name = taskList.name + "   (${taskList.quantityOfCompletedTasks}✔/${totalTasks})"
            binding.taskListName.text = name
            binding.root.setOnClickListener{
                clickListener.setTaskList(taskList)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTaskListItem(listOfTaskLists[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TaskListItemBinding.inflate(inflater, parent, false)
        return ViewHolder(parent.context, binding, clickListener)
    }

    override fun getItemCount(): Int = listOfTaskLists.size
}

interface TaskListClickListener {
    fun setTaskList(taskList: TaskList)
}
