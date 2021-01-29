package com.zhuinden.simplestackexamplemvvm.features.taskdetail

import android.os.Bundle

import android.view.View
import com.zhuinden.simplestackexamplemvvm.R
import com.zhuinden.simplestackexamplemvvm.databinding.TaskdetailFragmentBinding
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup

class TaskDetailFragment : KeyedFragment(R.layout.taskdetail_fragment) {
    private val viewModel by lazy { lookup<TaskDetailViewModel>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = TaskdetailFragmentBinding.bind(view)

        val key = getKey<TaskDetailKey>()

        with(binding) {
            this.taskDetailTitle.text = key.task.title
            this.taskDetailDescription.text = key.task.description
            this.taskDetailComplete.isChecked = key.task.isCompleted

            this.taskDetailComplete.setOnCheckedChangeListener { buttonView, isChecked ->
                viewModel.onTaskCheckChanged(isChecked)
            }

            fab.setOnClickListener {
                viewModel.onEditTaskClicked()
            }
        }

        binding.toolbar.root.addExtraAction("Delete", R.drawable.ic_baseline_delete_white_24) {
            viewModel.onDeleteTaskClicked()
        }
    }
}