package com.example.taskorganiserapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.taskorganiserapp.databinding.SettingsFragmentBinding

class Settings(private val preferences: SharedPreferencesManager, private val viewModel: TaskViewModel, private val taskList: TaskList): Fragment() {
    private lateinit var binding: SettingsFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ArrayAdapter.createFromResource(this.requireContext(), R.array.sortTypes, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinner.adapter = adapter
        }
        binding.spinner.setSelection(preferences.getSortType())
        Log.i("sortType", "selected sorttype: " + preferences.getSortType().toString())

        binding.backButton.setOnClickListener {

            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                preferences.setSortType(position)
                viewModel.setTasksFromTaskList(taskList, 0)
                Log.i("sortType", position.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        super.onViewCreated(view, savedInstanceState)
    }
}