package com.example.taskorganiserapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
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

class MainActivity : AppCompatActivity(), TaskItemClickListener, TaskListClickListener, SubtaskItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sideSheetDialog: SideSheetDialog
    private lateinit var sideSheetBinding: SideViewOfTasklistBinding
    private lateinit var currentTaskList: TaskList
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModel.TaskModelFactory((application as TaskOrganiserApp).repository)
    }
    private val taskListViewModel: TaskListViewModel by viewModels {
        TaskListModelFactory((application as TaskOrganiserApp).repository)
    }

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        sharedPreferencesManager = SharedPreferencesManager(TaskOrganiserApp.appContext)
        sideSheetDialog = SideSheetDialog(this)
        setContentView(binding.root)

        if(sharedPreferencesManager.isFirstRun()) {
            taskListViewModel.addTaskList(TaskList(name = "Wszystkie", isEditable = false))
        }

        currentTaskList = TaskList(name = "Wszystkie", isEditable = false)

        setSideSheet()
        setRecyclerView()
        setGestures()

        binding.topAppBar.title = currentTaskList.name

        binding.addTaskFAB.setOnClickListener {
            TaskCreator(null, currentTaskList.id, taskViewModel).show(supportFragmentManager, "newTaskTag")
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
                    if(currentTaskList.isEditable) {
                        taskListViewModel.deleteTaskList(currentTaskList)
                        sideSheetBinding.taskLists.adapter?.notifyItemRemoved(taskListViewModel.listOfTaskLists.value!!.indexOf(currentTaskList))
                        taskViewModel.setAllTasks()
                        currentTaskList = taskListViewModel.listOfTaskLists.value!!.first()
                        binding.topAppBar.title = taskListViewModel.listOfTaskLists.value!!.first().name
                    }
                    else
                        Toast.makeText(this, "Cannot delete this list", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.search -> {
                    val alertDialog = AlertDialog.Builder(this)
                    val inflater = layoutInflater
                    val dialogLayout = inflater.inflate(R.layout.add_list_dialog, null)
                    val nameField = dialogLayout.findViewById<EditText>(R.id.newListName)
                    with(alertDialog) {
                        setTitle("Search task by name")
                        setPositiveButton("OK") { _, _ ->
                            val name = nameField.text.toString()
                            taskViewModel.findTasksByName(name)
                            sideSheetDialog.dismiss()
                            Log.i("search", "Found by name: $name")
                        }
                        setNegativeButton("Cancel") { _, _ -> }
                        setView(dialogLayout)
                        show()
                    }
                    true
                }
                R.id.settings -> {
                    val settings = Settings(sharedPreferencesManager, taskViewModel, currentTaskList)
                    supportFragmentManager.beginTransaction()
                        .replace(android.R.id.content, settings)
                        .addToBackStack(null)
                        .commit()
                    true
                }
                else -> false
            }
        }
    }

    private fun setRecyclerView() {
        val mainActivity = this
        taskViewModel.taskItems.observe(this) {
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
        sideSheetBinding = SideViewOfTasklistBinding.inflate(layoutInflater)
        val mainActivity = this

        sideSheetDialog.isDismissWithSheetAnimationEnabled = true
        sideSheetDialog.setSheetEdge(Gravity.START)
        sideSheetDialog.setCanceledOnTouchOutside(true)
        sideSheetDialog.setContentView(sideSheetBinding.root)

        sideSheetBinding.backButton.setOnClickListener {
            sideSheetDialog.dismiss()
        }
        sideSheetBinding.addTaskListButton.setOnClickListener{
            val newTaskList = TaskList(name = "Lista", isEditable = true)
            val alertDialog = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.add_list_dialog, null)
            val name = dialogLayout.findViewById<EditText>(R.id.newListName)
            with(alertDialog) {
                setTitle("Add new list")
                setPositiveButton("OK") { _, _ ->
                    newTaskList.name = name.text.toString()
                    taskListViewModel.addTaskList(newTaskList)
                    Log.i("TaskList", "lastInserted=" + taskListViewModel.lastInsertedID)
                    newTaskList.id = taskListViewModel.lastInsertedID
                    currentTaskList = newTaskList
                    binding.topAppBar.title = currentTaskList.name
                    taskViewModel.setTasksFromTaskList(currentTaskList)
                    sideSheetDialog.dismiss()
                    Log.i("TaskList", "Added TaskList id=" + newTaskList.id + " name=" + newTaskList.name)
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
            @SuppressLint("NotifyDataSetChanged")
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedTask: TaskItem = taskViewModel.taskItems.value!![viewHolder.adapterPosition]

                taskViewModel.deleteTaskItem(deletedTask)
                binding.taskList.adapter?.notifyItemRemoved(viewHolder.adapterPosition)

                Snackbar.make(binding.taskList, "Deleted task: " + deletedTask.name, Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        taskViewModel.addTaskItem(deletedTask)
                        binding.taskList.adapter?.notifyDataSetChanged()
                    }.show()
            }
        }).attachToRecyclerView(binding.taskList)
    }

    override fun editTaskItem(task: TaskItem) {
        TaskCreator(task, currentTaskList.id, taskViewModel).show(supportFragmentManager, "editTaskTag")
    }

    override fun setCompleteTaskItem(task: TaskItem) {
        taskViewModel.setState(task, true)
    }

    override fun setIncompleteTaskItem(task: TaskItem) {
        taskViewModel.setState(task, false)
    }

    override fun setTaskList(taskList: TaskList) {
        currentTaskList = taskList
        binding.topAppBar.title = taskList.name

        if(currentTaskList.name == "Wszystkie")
            taskViewModel.setAllTasks()
        else
            taskViewModel.setTasksFromTaskList(taskList)

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