package com.example.taskorganiserapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent.EDGE_LEFT
import android.view.MotionEvent.EDGE_RIGHT
import android.view.View
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskorganiserapp.databinding.ActivityMainBinding
import com.google.android.material.sidesheet.SideSheetBehavior
import com.google.android.material.sidesheet.SideSheetCallback
import com.google.android.material.sidesheet.SideSheetDialog

class MainActivity : AppCompatActivity(), TaskItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var taskListViewModel: TaskListViewModel
    private lateinit var sideSheetDialog: SideSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        taskListViewModel = ViewModelProvider(this)[TaskListViewModel::class.java]
        sideSheetDialog = SideSheetDialog(this)

        setRecyclersView()
        setSideSheet()

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
                adapter = TaskItemAdapter(it, mainActivity)
            }
        }
        taskListViewModel.taskLists.observe(this){
            sideSheetDialog.
        }
    }

    @SuppressLint("InflateParams")
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

        val inflater = layoutInflater.inflate(R.layout.side_view_of_tasklist, null)
        val backButton = inflater.findViewById<ImageButton>(R.id.backButton)

        backButton.setOnClickListener {
            sideSheetDialog.dismiss()
        }
        sideSheetDialog.isDismissWithSheetAnimationEnabled = true
        sideSheetDialog.setSheetEdge(Gravity.START)
        sideSheetDialog.setCanceledOnTouchOutside(true)
        sideSheetDialog.setContentView(inflater)
    }
}