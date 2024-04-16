package com.example.taskorganiserapp.View

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskorganiserapp.Model.Entities.TaskList
import com.example.taskorganiserapp.Model.TaskOrganiserApp
import com.example.taskorganiserapp.R
import com.example.taskorganiserapp.ViewModel.Adapters.TaskListClickListener
import com.example.taskorganiserapp.ViewModel.Adapters.TaskListsAdapter
import com.example.taskorganiserapp.ViewModel.TaskListModelFactory
import com.example.taskorganiserapp.ViewModel.TaskListViewModel
import com.example.taskorganiserapp.ViewModel.TaskViewModel
import com.example.taskorganiserapp.databinding.SideViewOfTasklistBinding
import com.google.android.material.sidesheet.SideSheetBehavior
import com.google.android.material.sidesheet.SideSheetCallback
import com.google.android.material.sidesheet.SideSheetDialog

class SideSheet(private val taskViewModel: TaskViewModel, private val taskListViewModel: TaskListViewModel, private val context: Context): SideSheetDialog(context), TaskListClickListener {
    private val binding: SideViewOfTasklistBinding = SideViewOfTasklistBinding.inflate(layoutInflater)
    private var dataDownloaded: Boolean = false

    init {
        this.behavior.addCallback(object : SideSheetCallback() {
            override fun onStateChanged(sheet: View, newState: Int) {
                if (newState == SideSheetBehavior.STATE_DRAGGING) {
                    this@SideSheet.behavior.state = SideSheetBehavior.STATE_EXPANDED
                }
            }
            override fun onSlide(sheet: View, slideOffset: Float) {
            }
        })
        this.isDismissWithSheetAnimationEnabled = true
        this.setSheetEdge(Gravity.START)
        this.setCanceledOnTouchOutside(true)
        this.setContentView(binding.root)
        setListeners()
        setObserver()
    }

    private fun setListeners() {
        binding.backButton.setOnClickListener {
            this.dismiss()
        }
        binding.addTaskListButton.setOnClickListener{
            val newTaskList = TaskList(name = "Lista", quantityOfToDoTasks = 0, quantityOfCompletedTasks = 0, isEditable = true)
            val alertDialog = AlertDialog.Builder(context)
            val dialogLayout = layoutInflater.inflate(R.layout.add_list_dialog, null)
            val name = dialogLayout.findViewById<EditText>(R.id.newListName)

            with(alertDialog) {
                setTitle("Dodaj nową listę")
                setPositiveButton("OK") { _, _ ->
                    newTaskList.name = name.text.toString()
                    taskListViewModel.addTaskList(newTaskList)
                    taskViewModel.selectedList.value = newTaskList
                    this@SideSheet.dismiss()
                    Log.i("TaskList", "Added TaskList id=" + newTaskList.id + " name=" + newTaskList.name)
                }
                setNegativeButton("Anuluj") { _, _ -> }
                setView(dialogLayout)
                show()
            }
        }
    }

    private fun setObserver() {
        taskListViewModel.listOfTaskLists.observeForever {
            if(!it.isNullOrEmpty() && !dataDownloaded) {
                taskViewModel.selectedList.value = taskListViewModel.listOfTaskLists.value!!.first()
                dataDownloaded = true
            }

            binding.taskLists.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = TaskListsAdapter(it, this@SideSheet)
            }
        }
    }

    override fun setTaskList(taskList: TaskList) {
        taskViewModel.selectedList.value = taskList
        this@SideSheet.dismiss()
    }
}