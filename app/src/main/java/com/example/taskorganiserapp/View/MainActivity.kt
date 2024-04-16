package com.example.taskorganiserapp.View

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskorganiserapp.Model.Entities.SubtaskItem
import com.example.taskorganiserapp.Model.Entities.TaskItem
import com.example.taskorganiserapp.Model.Entities.TaskList
import com.example.taskorganiserapp.Model.Services.SharedPreferencesManager
import com.example.taskorganiserapp.Model.TaskOrganiserApp
import com.example.taskorganiserapp.R
import com.example.taskorganiserapp.ViewModel.Adapters.SubtaskItemClickListener
import com.example.taskorganiserapp.ViewModel.Adapters.TaskItemAdapter
import com.example.taskorganiserapp.ViewModel.Adapters.TaskItemClickListener
import com.example.taskorganiserapp.ViewModel.Adapters.TaskListClickListener
import com.example.taskorganiserapp.ViewModel.TaskListModelFactory
import com.example.taskorganiserapp.ViewModel.TaskListViewModel
import com.example.taskorganiserapp.ViewModel.Adapters.TaskListsAdapter
import com.example.taskorganiserapp.ViewModel.TaskModelFactory
import com.example.taskorganiserapp.ViewModel.TaskViewModel
import com.example.taskorganiserapp.databinding.ActivityMainBinding
import com.example.taskorganiserapp.databinding.SideViewOfTasklistBinding
import com.google.android.material.sidesheet.SideSheetBehavior
import com.google.android.material.sidesheet.SideSheetCallback
import com.google.android.material.sidesheet.SideSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), TaskItemClickListener, SubtaskItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sideSheetDialog: SideSheet
    private lateinit var appBarActions: AppBar
    private val sharedPreferencesManager = SharedPreferencesManager(TaskOrganiserApp.appContext)
    private val taskListViewModel: TaskListViewModel by viewModels {
        TaskListModelFactory((application as TaskOrganiserApp).repository)
    }
    val taskViewModel: TaskViewModel by viewModels {
        TaskModelFactory((application as TaskOrganiserApp).repository)
    }
    var tabPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appBarActions = AppBar(taskListViewModel, taskViewModel, this)
        sideSheetDialog = SideSheet(taskViewModel, taskListViewModel, this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setObservers()

        if(sharedPreferencesManager.isFirstRun()) {
            val defaultTaskList = TaskList(name = "Wszystkie", quantityOfCompletedTasks = 0, quantityOfToDoTasks =  0, isEditable = false)
            taskListViewModel.addTaskList(defaultTaskList)
            taskViewModel.selectedList.value = defaultTaskList
        }
        else {
            taskListViewModel.firstTaskList.observe(this) {
                taskViewModel.selectedList.value = taskListViewModel.firstTaskList.value
                taskListViewModel.firstTaskList.removeObservers(this@MainActivity)
            }
        }

        lifecycleScope.launch {
            taskViewModel.selectedList.collect {
                if(it != null) {
                    binding.topAppBar.title = taskViewModel.selectedList.value!!.name
                    taskViewModel.taskItems.removeObservers(this@MainActivity)
                    taskViewModel.selectedToDoTaskItems.removeObservers(this@MainActivity)
                    taskViewModel.selectedCompletedTaskItems.removeObservers(this@MainActivity)
                    taskViewModel.setTasksFromTaskList(taskViewModel.selectedList.value!!, tabPosition)
                    setObservers()
                }
            }
        }
        setGestures()

        binding.addTaskFAB.setOnClickListener {
            val taskCreator = TaskCreator(null, taskViewModel.selectedList.value!!.id, taskViewModel)
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
            appBarActions.editTaskListName(taskViewModel.selectedList.value!!)
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.deleteList -> {
                    taskViewModel.selectedList.value = appBarActions.deleteTaskList(taskViewModel.selectedList.value!!, tabPosition)
                    true
                }
                R.id.search -> {
                    val alertDialog = AlertDialog.Builder(this)
                    val inflater = layoutInflater
                    val dialogLayout = inflater.inflate(R.layout.add_list_dialog, null)
                    val nameField = dialogLayout.findViewById<EditText>(R.id.newListName)
                    with(alertDialog) {
                        setTitle("Wyszukaj zadanie po nazwie")
                        setPositiveButton("OK") { _, _ ->
                            val name = nameField.text.toString()
                            taskViewModel.findTasksByName(name)
                            sideSheetDialog.dismiss()
                            Log.i("search", "Found by name: $name")
                        }
                        setNegativeButton("Anuluj") { _, _ -> }
                        setView(dialogLayout)
                        show()
                    }
                    true
                }
                R.id.settings -> {
                    val settings = Settings(sharedPreferencesManager, taskViewModel, taskViewModel.selectedList.value!!)
                    supportFragmentManager.beginTransaction()
                        .replace(android.R.id.content, settings)
                        .addToBackStack(null)
                        .commit()
                    true
                }
                R.id.share -> {
                    appBarActions.shareTaskList(taskViewModel.selectedList.value!!)
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
            val tabName = "Do zrobienia (${taskViewModel.selectedToDoTaskItems.value?.size ?:0})"
            binding.tabLayout.getTabAt(0)?.text = tabName
            if(taskViewModel.selectedList.value != null) {
                if(taskViewModel.selectedToDoTaskItems.value?.size != null)
                    taskViewModel.selectedList.value!!.quantityOfToDoTasks = taskViewModel.selectedToDoTaskItems.value?.size!!
                else
                    taskViewModel.selectedList.value!!.quantityOfToDoTasks = 0
                taskListViewModel.updateTaskList(taskViewModel.selectedList.value!!)
            }
        }
        taskViewModel.selectedCompletedTaskItems.observe(this) {
            val tabName = "Wykonane (${taskViewModel.selectedCompletedTaskItems.value?.size ?:0})"
            binding.tabLayout.getTabAt(1)?.text = tabName
            if(taskViewModel.selectedList.value != null) {
                if(taskViewModel.selectedCompletedTaskItems.value?.size != null)
                    taskViewModel.selectedList.value!!.quantityOfCompletedTasks = taskViewModel.selectedCompletedTaskItems.value?.size!!
                else
                    taskViewModel.selectedList.value!!.quantityOfCompletedTasks = 0
                taskListViewModel.updateTaskList(taskViewModel.selectedList.value!!)
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

                Snackbar.make(binding.taskList, "Usunięto zadanie: " + deletedTask.name, Snackbar.LENGTH_LONG)
                    .setAction("COFNIJ") {
                        taskViewModel.addTaskItem(deletedTask)
                        binding.taskList.adapter?.notifyDataSetChanged()
                    }.show()
            }
        }).attachToRecyclerView(binding.taskList)
    }

    override fun editTaskItem(task: TaskItem) {
        TaskCreator(task, taskViewModel.selectedList.value!!.id, taskViewModel).show(supportFragmentManager, "editTaskTag")
    }

    override fun setCompleteTaskItem(task: TaskItem) {
        taskViewModel.setState(task, true)
    }

    override fun setIncompleteTaskItem(task: TaskItem) {
        taskViewModel.setState(task, false)
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