package com.zhuinden.simplestackexamplemvvm.features.taskdetail


import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.zhuinden.simplestackexamplemvvm.R
import com.zhuinden.simplestackexamplemvvm.databinding.TaskdetailFragmentBinding
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup

class TaskDetailFragment : KeyedFragment(R.layout.taskdetail_fragment) {
    private val viewModel by lazy { lookup<TaskDetailViewModel>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete -> {
                viewModel.onDeleteTaskClicked()
                return true
            }
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.taskdetail_fragment_menu, menu)
    }

    fun onEditTaskClicked() {
        viewModel.onEditTaskClicked()
    }
}