package com.zhuinden.simplestackexamplemvvm.features.tasks

import android.view.View
import android.widget.CompoundButton
import com.xwray.groupie.viewbinding.BindableItem
import com.zhuinden.simplestackexamplemvvm.R
import com.zhuinden.simplestackexamplemvvm.data.Task
import com.zhuinden.simplestackexamplemvvm.databinding.TaskItemBinding
import com.zhuinden.simplestackexamplemvvm.features.taskdetail.TaskDetailKey
import com.zhuinden.simplestackextensions.navigatorktx.backstack

class TaskItem(
    private val task: Task,
    private val listener: Listener
) : BindableItem<TaskItemBinding>() {
    interface Listener {
        fun onTaskCheckChanged(task: Task, isChecked: Boolean)
    }

    private val onCheckChanged = CompoundButton.OnCheckedChangeListener { view, isChecked ->
        listener.onTaskCheckChanged(task, isChecked)
    }

    private val onClick = View.OnClickListener { view ->
        view.backstack.goTo(TaskDetailKey(task))
    }

    override fun getLayout(): Int = R.layout.task_item

    override fun initializeViewBinding(view: View): TaskItemBinding = TaskItemBinding.bind(view)

    override fun bind(viewBinding: TaskItemBinding, position: Int) {
        with(viewBinding) {
            this.title.text = task.titleForList
            this.complete.setOnCheckedChangeListener(null)
            this.complete.isChecked = task.isCompleted
            this.complete.setOnCheckedChangeListener(onCheckChanged)
            root.setOnClickListener(onClick)
        }
    }
}