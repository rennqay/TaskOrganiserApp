package com.example.taskorganiserapp.View

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.example.taskorganiserapp.Model.Entities.TaskItem
import com.example.taskorganiserapp.Model.Entities.TaskList
import com.example.taskorganiserapp.R
import com.example.taskorganiserapp.ViewModel.TaskListViewModel
import com.example.taskorganiserapp.ViewModel.TaskViewModel
import com.example.taskorganiserapp.databinding.ActivityMainBinding
import com.example.taskorganiserapp.databinding.SideViewOfTasklistBinding
import java.io.File
import java.io.FileWriter

class AppBar(private val taskListViewModel: TaskListViewModel, private val taskViewModel: TaskViewModel, private val context: Context)
{
    private val activityMainBinding: ActivityMainBinding = ActivityMainBinding.inflate(LayoutInflater.from(context))
    private val sideSheetBinding: SideViewOfTasklistBinding = SideViewOfTasklistBinding.inflate(LayoutInflater.from(context))

    fun deleteTaskList(selectedTaskList: TaskList, tabPosition: Int): TaskList {
        return if(selectedTaskList.isEditable) {
            taskListViewModel.deleteTaskList(selectedTaskList)
            sideSheetBinding.taskLists.adapter?.notifyItemRemoved(taskListViewModel.listOfTaskLists.value!!.indexOf(selectedTaskList))
            val defaultTaskList = taskListViewModel.listOfTaskLists.value!!.first()
            taskViewModel.setTasksFromTaskList(defaultTaskList, tabPosition)
            activityMainBinding.topAppBar.title = taskListViewModel.listOfTaskLists.value!!.first().name
            defaultTaskList
        } else {
            Toast.makeText(context, "Cannot delete this list", Toast.LENGTH_SHORT).show()
            selectedTaskList
        }
    }

    fun editTaskListName(selectedTaskList: TaskList) {
        val alertDialog = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(R.layout.add_list_dialog, null)
        val name = dialogLayout.findViewById<EditText>(R.id.newListName)
        with(alertDialog) {
            setTitle("Edit list name")
            setPositiveButton("OK") { _, _ ->
                selectedTaskList.name = name.text.toString()
                taskListViewModel.updateTaskList(selectedTaskList)
                activityMainBinding.topAppBar.title = selectedTaskList.name
            }
            setNegativeButton("Cancel") { _, _ -> }
            setView(dialogLayout)
            show()
        }
    }

    fun shareTaskList(selectedTaskList: TaskList) {
        val toDoTasks = taskViewModel.selectedToDoTaskItems.value?.toList()
        val completedTasks = taskViewModel.selectedCompletedTaskItems.value?.toList()

        if(toDoTasks.isNullOrEmpty() && completedTasks.isNullOrEmpty())
            Toast.makeText(context, "List is empty!", Toast.LENGTH_SHORT).show()
        else {
            val uri = prepareUri(selectedTaskList.name, toDoTasks, completedTasks)

            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "text/*"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            context.startActivity(Intent.createChooser(intent, "Share to: "))
        }
    }

    private fun prepareUri(fileName: String, toDoTasks: List<TaskItem>?, completedTasks: List<TaskItem>?): Uri {
        val file = File(context.getExternalFilesDir(null), "$fileName.txt")
        val fileWriter = FileWriter(file)
        val uri = FileProvider.getUriForFile(context, "com.example.taskorganiserapp.provider", file)

        fileWriter.append("TO DO\n\n")
        if(toDoTasks != null)
            writeToFile(fileWriter, toDoTasks)

        fileWriter.append("COMPLETED\n\n")
        if(completedTasks != null)
            writeToFile(fileWriter, completedTasks)

        fileWriter.flush()
        fileWriter.close()

        return uri
    }

    private fun writeToFile(writer: FileWriter, tasks: List<TaskItem>) {
        for(i in tasks.indices) {
            writer.append("${i+1}. " + tasks[i].name + "\n")

            if(!tasks[i].note.isNullOrEmpty())
                writer.append("Note: " + tasks[i].note + "\n")

            if(tasks[i].date != null) {
                writer.append("Deadline: ")

                if(tasks[i].time != null)
                    writer.append(tasks[i].time.toString() + " ")

                writer.append(tasks[i].date.toString() + "\n")
            }
            writer.append("Priority: " + tasks[i].getPriorityInString() + "\n")

            if(!tasks[i].subtasks.isNullOrEmpty()) {
                writer.append("Subtasks:\n")

                for (j in tasks[i].subtasks!!.indices) {
                    writer.append(" - " + tasks[i].subtasks!![j].name + " - " + tasks[i].subtasks!![j].convertCompletedToString() + "\n")
                }
            }
            writer.append("\n")
        }
    }
}