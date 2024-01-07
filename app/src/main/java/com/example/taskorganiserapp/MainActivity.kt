package com.example.taskorganiserapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskorganiserapp.databinding.ActivityMainBinding
import com.example.taskorganiserapp.databinding.SideViewOfTasklistBinding
import com.google.android.material.sidesheet.SideSheetBehavior
import com.google.android.material.sidesheet.SideSheetCallback
import com.google.android.material.sidesheet.SideSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity(), TaskItemClickListener, TaskListClickListener, SubtaskItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sideSheetDialog: SideSheetDialog
    private lateinit var sideSheetBinding: SideViewOfTasklistBinding
    private lateinit var currentTaskList: TaskList
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var appBarActions: AppBarActions
    private val taskListViewModel: TaskListViewModel by viewModels {
        TaskListModelFactory((application as TaskOrganiserApp).repository)
    }
    val taskViewModel: TaskViewModel by viewModels {
        TaskViewModel.TaskModelFactory((application as TaskOrganiserApp).repository)
    }
    var tabPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        sharedPreferencesManager = SharedPreferencesManager(TaskOrganiserApp.appContext)
        sideSheetDialog = SideSheetDialog(this)
        appBarActions = AppBarActions(taskListViewModel, taskViewModel, this)
        setContentView(binding.root)

        if(sharedPreferencesManager.isFirstRun()) {
            taskListViewModel.addTaskList(TaskList(name = "Wszystkie", quantityOfCompletedTasks = 0, quantityOfToDoTasks =  0, isEditable = false))
            currentTaskList = TaskList(name = "Wszystkie", quantityOfCompletedTasks = 0, quantityOfToDoTasks =  0, isEditable = false)
        }
        currentTaskList = TaskList(name = "Wszystkie", quantityOfCompletedTasks = 0, quantityOfToDoTasks =  0, isEditable = false)
        binding.topAppBar.title = currentTaskList.name

        setSideSheet()
        setObservers()
        setGestures()

        binding.addTaskFAB.setOnClickListener {
            val taskCreator = TaskCreator(null, currentTaskList.id, taskViewModel)
            taskCreator.show(supportFragmentManager, "newTaskTag")
        }

        binding.topAppBar.setNavigationOnClickListener {
            sideSheetDialog.show()
        }

        binding.tabLayout.addOnTabSelectedListener (object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tabPosition = tab!!.position
                    taskViewModel.setTasksFromTabPosition(tabPosition)
                }
                override fun onTabReselected(tab: TabLayout.Tab?) {}
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
            })

        binding.topAppBar.setOnClickListener {
            appBarActions.editTaskListName(currentTaskList)
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.deleteList -> {
                    currentTaskList = appBarActions.deleteTaskList(currentTaskList, tabPosition)
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
                R.id.share -> {
                    appBarActions.shareTaskList(currentTaskList)
                    true
                }
                else -> false
            }
        }
    }

    private fun setObservers() {
        taskViewModel.taskItems.observe(this) {
            binding.taskList.apply {
                layoutManager = LinearLayoutManager(applicationContext)
                adapter = TaskItemAdapter(it, this@MainActivity, this@MainActivity)
            }
        }
        taskViewModel.selectedToDoTaskItems.observe(this) {
            val tabName = "To Do (${taskViewModel.selectedToDoTaskItems.value?.size})"
            binding.tabLayout.getTabAt(0)?.text = tabName
            currentTaskList.quantityOfToDoTasks = taskViewModel.selectedToDoTaskItems.value?.size!!
            taskListViewModel.updateTaskList(currentTaskList)
        }
        taskViewModel.selectedCompletedTaskItems.observe(this) {
            val tabName = "Completed (${taskViewModel.selectedCompletedTaskItems.value?.size})"
            binding.tabLayout.getTabAt(1)?.text = tabName
            currentTaskList.quantityOfCompletedTasks = taskViewModel.selectedCompletedTaskItems.value?.size!!
            taskListViewModel.updateTaskList(currentTaskList)
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
        sideSheetDialog.isDismissWithSheetAnimationEnabled = true
        sideSheetDialog.setSheetEdge(Gravity.START)
        sideSheetDialog.setCanceledOnTouchOutside(true)
        sideSheetDialog.setContentView(sideSheetBinding.root)

        taskListViewModel.listOfTaskLists.observe(this) {
            sideSheetBinding.taskLists.apply {
                layoutManager = LinearLayoutManager(applicationContext)
                adapter = TaskListsAdapter(it, this@MainActivity)
            }
        }

        sideSheetBinding.backButton.setOnClickListener {
            sideSheetDialog.dismiss()
        }
        sideSheetBinding.addTaskListButton.setOnClickListener{
            val newTaskList = TaskList(name = "Lista", quantityOfToDoTasks = 0, quantityOfCompletedTasks = 0, isEditable = true)
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
                    taskViewModel.setTasksFromTaskList(currentTaskList, tabPosition)
                    sideSheetDialog.dismiss()
                    Log.i("TaskList", "Added TaskList id=" + newTaskList.id + " name=" + newTaskList.name)
                }
                setNegativeButton("Cancel") { _, _ -> }
                setView(dialogLayout)
                show()
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
        taskViewModel.taskItems.removeObservers(this)
        taskViewModel.selectedToDoTaskItems.removeObservers(this)
        taskViewModel.selectedCompletedTaskItems.removeObservers(this)
        taskViewModel.setTasksFromTaskList(taskList, tabPosition)
        setObservers()
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