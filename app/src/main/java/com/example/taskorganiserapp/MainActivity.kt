package com.example.taskorganiserapp

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskorganiserapp.databinding.ActivityMainBinding
import com.example.taskorganiserapp.databinding.SideViewOfTasklistBinding
import com.google.android.material.sidesheet.SideSheetBehavior
import com.google.android.material.sidesheet.SideSheetCallback
import com.google.android.material.sidesheet.SideSheetDialog
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

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
        currentTaskList = taskListViewModel.listOfTaskLists.value!![0]

        setSideSheet()
        setRecyclerView()
        setGestures()

        binding.topAppBar.title = currentTaskList.name

        binding.addTaskFAB.setOnClickListener {
            TaskCreator(null).show(supportFragmentManager, "newTaskTag")
        }

        binding.topAppBar.setNavigationOnClickListener {
            sideSheetDialog.show()
        }

        binding.topAppBar.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.add_list_dialog, null)
            val name = dialogLayout.findViewById<EditText>(R.id.newListName)
            with(alertDialog) {
                setTitle("Edit list name")
                setPositiveButton("OK") { _, _ ->
                    currentTaskList.name = name.text.toString()
                    taskListViewModel.updateTaskList(currentTaskList)
                    binding.topAppBar.title = currentTaskList.name
                }
                setNegativeButton("Cancel") { _, _ -> }
                setView(dialogLayout)
                show()
            }
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.deleteList -> {
                    if(currentTaskList != taskListViewModel.listOfTaskLists.value!!.first()) {
                        taskListViewModel.deleteTaskList(currentTaskList)
                        taskViewModel.setTaskList(taskListViewModel.listOfTaskLists.value!!.first())
                        currentTaskList = taskListViewModel.listOfTaskLists.value!!.first()
                        binding.topAppBar.title = taskListViewModel.listOfTaskLists.value!!.first().name
                    }
                    else
                        Toast.makeText(this, "Cannot delete first list", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.search -> {
                    // Handle search icon press
                    true
                }
                R.id.settings -> {
                    // Handle more item (inside overflow menu) press
                    true
                }
                else -> false
            }
        }
    }

    private fun setRecyclerView() {
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

        sideSheetDialog.isDismissWithSheetAnimationEnabled = true
        sideSheetDialog.setSheetEdge(Gravity.START)
        sideSheetDialog.setCanceledOnTouchOutside(true)
        sideSheetDialog.setContentView(sideSheetBinding.root)

        sideSheetBinding.backButton.setOnClickListener {
            sideSheetDialog.dismiss()
        }
        sideSheetBinding.addTaskListButton.setOnClickListener{
            val newTaskList = TaskList("Lista", mutableListOf(), true)
            val alertDialog = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.add_list_dialog, null)
            val name = dialogLayout.findViewById<EditText>(R.id.newListName)
            with(alertDialog) {
                setTitle("Add new list")
                setPositiveButton("OK") { _, _ ->
                    newTaskList.name = name.text.toString()
                    taskListViewModel.addTaskList(newTaskList)
                    setTaskList(newTaskList)
                }
                setNegativeButton("Cancel") { _, _ -> }
                setView(dialogLayout)
                show()
            }
        }

        taskListViewModel.listOfTaskLists.observe(this) {
            sideSheetBinding.taskLists.apply {
                layoutManager = LinearLayoutManager(applicationContext)
                adapter = TaskListsAdapter(it, mainActivity)
            }
        }
    }

    private fun setGestures() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedTask: TaskItem = taskViewModel.taskItems.value!![viewHolder.adapterPosition]
                val position = viewHolder.adapterPosition
                val listOfDeleted = taskListViewModel.listOfTaskLists.value!![1].tasks.toMutableList()

                if(currentTaskList.tasks != listOfDeleted) {
                    listOfDeleted.add(deletedTask)
                    taskListViewModel.listOfTaskLists.value!![1].tasks = listOfDeleted
                }
                taskViewModel.taskItems.value!!.removeAt(viewHolder.adapterPosition)
                binding.taskList.adapter?.notifyItemRemoved(viewHolder.adapterPosition)

                Snackbar.make(binding.taskList, "Deleted task: " + deletedTask.name, Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        taskViewModel.taskItems.value!!.add(position, deletedTask)
                        binding.taskList.adapter?.notifyItemInserted(position)
                    }.show()
            }
        }).attachToRecyclerView(binding.taskList)
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

    override fun setTaskList(taskList: TaskList) {
        currentTaskList.tasks = taskViewModel.taskItems.value!!
        taskViewModel.setTaskList(taskList)
        currentTaskList = taskList
        binding.topAppBar.title = taskList.name
        sideSheetDialog.dismiss()
    }

    override fun deleteSubtaskItem(subtask: SubtaskItem) {

    }

    override fun setCompleteSubtaskItem(subtask: SubtaskItem) {
        subtask.completed = true
    }

    override fun setIncompleteSubtaskItem(subtask: SubtaskItem) {
        subtask.completed = false
    }
}