package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.tasks

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.presentation.objects.Task
import com.zhuinden.simplestackdemoexamplefragments.util.Preconditions.checkNotNull
import com.zhuinden.simplestackdemoexamplefragments.util.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.task_item.*
import org.jetbrains.anko.sdk15.listeners.onClick
import java.util.*

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

class TasksAdapter(
    private var tasks: List<Task>,
    private val itemListener: TaskItemListener
) : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    var data: List<Task>
        get() = Collections.unmodifiableList(tasks)
        set(tasks) {
            this.tasks = checkNotNull(tasks)
        }

    class TaskViewHolder(
        override val containerView: View,
        private val itemListener: TaskItemListener
    ) : LayoutContainer, RecyclerView.ViewHolder(containerView) {
        lateinit var task: Task

        private val rowClickListener = View.OnClickListener { _ -> itemListener.openTask(task) }

        private val context = containerView.context

        init {
            containerView.setOnClickListener(rowClickListener)
            complete.onClick {
                if (!task.isCompleted) {
                    itemListener.completeTask(task)
                } else {
                    itemListener.uncompleteTask(task)
                }
            }
        }

        fun bind(task: Task) {
            this.task = task
            title.text = task.titleForList
            complete.isChecked = task.isCompleted
            containerView.setBackgroundDrawable(ContextCompat.getDrawable(context, when {
                task.isCompleted -> R.drawable.list_completed_touch_feedback
                else -> R.drawable.touch_feedback
            }))
        }
    }

    interface TaskItemListener {
        fun openTask(task: Task)

        fun completeTask(task: Task)

        fun uncompleteTask(task: Task)
    }

    init {
        data = tasks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder =
        TaskViewHolder(parent.inflate(R.layout.task_item), itemListener)


    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task)
    }

    override fun getItemCount(): Int = tasks.size
}
