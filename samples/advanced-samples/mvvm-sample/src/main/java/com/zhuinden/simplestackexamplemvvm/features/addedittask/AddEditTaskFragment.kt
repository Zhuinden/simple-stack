package com.zhuinden.simplestackexamplemvvm.features.addedittask

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import com.zhuinden.simplestackexamplemvvm.R
import com.zhuinden.simplestackexamplemvvm.databinding.AddtaskFragmentBinding
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup

class AddEditTaskFragment : KeyedFragment(R.layout.addtask_fragment) {
    private val viewModel by lazy { lookup<AddEditTaskViewModel>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = AddtaskFragmentBinding.bind(view)

        with(binding) {
            addTaskTitle.setText(viewModel.title)
            addTaskDescription.setText(viewModel.description)

            addTaskTitle.doAfterTextChanged {
                viewModel.title = it.toString()
            }

            addTaskDescription.doAfterTextChanged {
                viewModel.description = it.toString()
            }
        }

        val key = getKey<AddEditTaskKey>()

        binding.toolbar.textTitle.text = when {
            key.task == null -> getString(R.string.add_task)
            else -> getString(R.string.edit_task)
        }

        binding.fab.setOnClickListener {
            viewModel.onSaveTaskClicked()
        }
    }
}