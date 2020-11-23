/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhuinden.simplestackexamplemvvm.features.addedittask


import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import com.zhuinden.liveevent.observe

import com.zhuinden.simplestackexamplemvvm.R
import com.zhuinden.simplestackexamplemvvm.databinding.AddtaskFragmentBinding
import com.zhuinden.simplestackexamplemvvm.util.showSnackbar
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup

/**
 * Main UI for the add task screen. Users can enter a task title and description.
 */
class AddEditTaskFragment : KeyedFragment(R.layout.addtask_fragment) {
    private val viewModel by lazy { lookup<AddEditTaskViewModel>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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

        viewModel.snackbarText.observe(viewLifecycleOwner) { snackBarText ->
            showSnackbar(binding.root, snackBarText)
        }
    }

    fun onSaveTaskClicked() {
        viewModel.onSaveTaskClicked()
    }
}