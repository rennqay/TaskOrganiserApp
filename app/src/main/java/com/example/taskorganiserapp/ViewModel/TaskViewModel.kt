package com.example.taskorganiserapp.ViewModel

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.taskorganiserapp.Model.Entities.TaskItem
import com.example.taskorganiserapp.Model.Entities.TaskList
import com.example.taskorganiserapp.Model.Database.TasksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class TaskViewModel(private val repository: TasksRepository): ViewModel() {
    var taskItems: MediatorLiveData<List<TaskItem>> = MediatorLiveData<List<TaskItem>>()
    var selectedToDoTaskItems: LiveData<List<TaskItem>>
    var selectedCompletedTaskItems: LiveData<List<TaskItem>>
    val selectedList: MutableStateFlow<TaskList?> = MutableStateFlow(null)

    init {
        selectedToDoTaskItems = repository.getTasksForList(TaskList(name = "Wszystkie", quantityOfToDoTasks = 0, quantityOfCompletedTasks = 0, isEditable = false)).asLiveData().map { tasks -> tasks.filter { task -> !task.isCompleted }}
        selectedCompletedTaskItems = repository.getTasksForList(TaskList(name = "Wszystkie", quantityOfToDoTasks = 0, quantityOfCompletedTasks = 0, isEditable = false)).asLiveData().map { tasks -> tasks.filter { task -> task.isCompleted }}
        taskItems.addSource(selectedToDoTaskItems) {
            taskItems.value = it
        }
        selectedList
    }

    fun setTasksFromTabPosition(tabPosition: Int) {
        if(tabPosition == 0) {
            taskItems.removeSource(selectedCompletedTaskItems)
            taskItems.addSource(selectedToDoTaskItems) {
                taskItems.value = it
            }
        }
        else {
            taskItems.removeSource(selectedToDoTaskItems)
            taskItems.addSource(selectedCompletedTaskItems) {
                taskItems.value = it
            }
        }
    }

    fun setTasksFromTaskList(taskList: TaskList, tabPosition: Int) {
        if(tabPosition == 0)
            taskItems.removeSource(selectedToDoTaskItems)

        if(tabPosition == 1)
            taskItems.removeSource(selectedCompletedTaskItems)
        selectedToDoTaskItems = repository.getTasksForList(taskList).asLiveData().map { tasks -> tasks.filter { task -> !task.isCompleted }}
        selectedCompletedTaskItems = repository.getTasksForList(taskList).asLiveData().map { tasks -> tasks.filter { task -> task.isCompleted }}

        if(tabPosition == 0)
            taskItems.addSource(selectedToDoTaskItems) {
                taskItems.value = it
            }
        else
            taskItems.addSource(selectedCompletedTaskItems) {
                taskItems.value = it
            }
    }

    fun findTasksByName(name: String) {
        taskItems.removeSource(selectedToDoTaskItems)
        taskItems.removeSource(selectedCompletedTaskItems)
        selectedToDoTaskItems = repository.getTasksByName(name).asLiveData().map { tasks -> tasks.filter { task -> !task.isCompleted }}
        selectedCompletedTaskItems = repository.getTasksByName(name).asLiveData().map { tasks -> tasks.filter { task -> task.isCompleted }}

        taskItems.addSource(selectedToDoTaskItems) {
            taskItems.value = it
        }
        taskItems.addSource(selectedCompletedTaskItems) {
            taskItems.value = it
        }
    }

    fun addTaskItem(newTaskItem: TaskItem) = viewModelScope.launch {
        repository.insertTaskItem(newTaskItem)
        repository.updateTaskListByID(newTaskItem.listID, 1, 0)
    }

    fun updateTaskItem(newTaskItem: TaskItem) = viewModelScope.launch {
        repository.updateTaskItem(newTaskItem)
    }

    fun deleteTaskItem(taskItem: TaskItem) = viewModelScope.launch {
        repository.deleteTaskItem(taskItem)
        if(taskItem.isCompleted)
            repository.updateTaskListByID(taskItem.listID, 0, -1)
        else
            repository.updateTaskListByID(taskItem.listID, -1, 0)

    }

    fun setState(taskItem: TaskItem, state: Boolean) = viewModelScope.launch {
        if(state) {
            taskItem.setCompletionTime()
            repository.updateTaskListByID(taskItem.listID, -1, 1)
        }
        else {
            taskItem.completionTime = null
            repository.updateTaskListByID(taskItem.listID, 1, -1)
        }
        taskItem.isCompleted = state
        repository.updateTaskItem(taskItem)
    }
}

class TaskModelFactory(private val repository: TasksRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java))
            return TaskViewModel(repository) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}