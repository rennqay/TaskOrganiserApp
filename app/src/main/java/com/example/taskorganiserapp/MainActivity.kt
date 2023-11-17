package com.example.taskorganiserapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskorganiserapp.databinding.ActivityMainBinding
import com.example.taskorganiserapp.databinding.SideViewOfTasklistBinding
import com.google.android.material.sidesheet.SideSheetBehavior
import com.google.android.material.sidesheet.SideSheetCallback
import com.google.android.material.sidesheet.SideSheetDialog

class MainActivity : AppCompatActivity(), TaskItemClickListener, TaskListClickListener, SubtaskItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var subtaskViewModel: SubtaskViewModel
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var taskListViewModel: TaskListViewModel
    private lateinit var sideSheetDialog: SideSheetDialog
    private lateinit var currentTaskList: TaskList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subtaskViewModel = ViewModelProvider(this)[SubtaskViewModel::class.java]
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        taskListViewModel = ViewModelProvider(this)[TaskListViewModel::class.java]
        sideSheetDialog = SideSheetDialog(this)
        currentTaskList = TaskList("Lista zadań", mutableListOf())
        taskListViewModel.addTaskList(currentTaskList)

        setSideSheet()
        setRecyclersView()

        binding.topAppBar.title = currentTaskList.name

        binding.addTaskFAB.setOnClickListener {
            TaskCreator(null).show(supportFragmentManager, "newTaskTag")
        }

        binding.topAppBar.setNavigationOnClickListener {
            sideSheetDialog.show()
        }
    }

    override fun editTaskItem(task: TaskItem) {
        TaskCreator(task).show(supportFragmentManager, "editTaskTag")
    }

    override fun setCompleteTaskItem(task: TaskItem) {
        taskViewModel.setCompleted(task)
    }

    override fun setIncompleteTaskItem(task: TaskItem) {
        taskViewModel.setUncompleted(task)
    }

    private fun setRecyclersView() {
        val mainActivity = this
        taskViewModel.taskItems.observe(this){
            binding.taskList.apply {
                layoutManager = LinearLayoutManager(applicationContext)
                adapter = TaskItemAdapter(it, mainActivity, mainActivity)
            }
        }

    }
    private fun setSideSheet() {
        sideSheetDialog.behavior.addCallback(object : SideSheetCallback() {
            override fun onStateChanged(sheet: View, newState: Int) {

                if (newState == SideSheetBehavior.STATE_DRAGGING) {
                    sideSheetDialog.behavior.state = SideSheetBehavior.STATE_EXPANDED
                }
            }
            override fun onSlide(sheet: View, slideOffset: Float) {
            }
        })
        val sideSheetBinding = SideViewOfTasklistBinding.inflate(layoutInflater)
        val mainActivity = this

        sideSheetBinding.backButton.setOnClickListener {
            sideSheetDialog.dismiss()
        }
        sideSheetBinding.addTaskListButton.setOnClickListener{
            taskListViewModel.addTaskList(TaskList("Lista zadań", mutableListOf()))
        }

        sideSheetDialog.isDismissWithSheetAnimationEnabled = true
        sideSheetDialog.setSheetEdge(Gravity.START)
        sideSheetDialog.setCanceledOnTouchOutside(true)
        sideSheetDialog.setContentView(sideSheetBinding.root)

        taskListViewModel.listOfTaskLists.observe(this) {
            sideSheetBinding.taskLists.apply {
                layoutManager = LinearLayoutManager(applicationContext)
                adapter = TaskListsAdapter(it, mainActivity)
            }
        }
    }

    override fun setTaskList(taskList: TaskList) {
        currentTaskList.tasks = taskViewModel.taskItems.value!!
        taskViewModel.setTaskList(taskList)
        currentTaskList = taskList
        binding.topAppBar.title = taskList.name
    }

    override fun deleteSubtaskItem(subtask: SubtaskItem) {

    }

    override fun setCompleteSubtaskItem(subtask: SubtaskItem) {
        subtaskViewModel.setCompleted(subtask)
    }

    override fun setIncompleteSubtaskItem(subtask: SubtaskItem) {
        subtaskViewModel.setUncompleted(subtask)
    }
}