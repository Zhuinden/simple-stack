package com.zhuinden.simplestackexamplemvvm.features.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.zhuinden.simplestackexamplemvvm.R
import com.zhuinden.simplestackexamplemvvm.data.Task
import com.zhuinden.simplestackexamplemvvm.databinding.TaskItemBinding
import com.zhuinden.simplestackexamplemvvm.features.taskdetail.TaskDetailKey
import com.zhuinden.simplestackextensions.navigatorktx.backstack

class TasksAdapter(
    private val listener: Listener,
    private var tasks: List<Task> = emptyList(),
) : BaseAdapter() {
    interface Listener {
        fun onTaskCheckChanged(task: Task, isChecked: Boolean)
    }

    fun replaceData(tasks: List<Task>) {
        setList(tasks)
    }

    override fun getCount(): Int = tasks.size

    override fun getItem(i: Int): Task = tasks[i]

    override fun getItemId(i: Int): Long = i.toLong()

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup): View {
        val task = getItem(position)
        val binding: TaskItemBinding = if (convertView == null) {
            // Create the binding
            val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.task_item, viewGroup, false)
            val binding = TaskItemBinding.bind(view)
            view.setTag(binding)
            binding
        } else {
            convertView.tag as TaskItemBinding
        }

        with(binding) {
            this.title.text = task.titleForList
            this.complete.setOnCheckedChangeListener(null)
            this.complete.isChecked = task.isCompleted
            this.complete.setOnCheckedChangeListener { view, isChecked ->
                listener.onTaskCheckChanged(task, isChecked)
            }

            root.setOnClickListener { view ->
                view.backstack.goTo(TaskDetailKey(task))
            }
        }

        return binding.root
    }

    private fun setList(tasks: List<Task>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }
}