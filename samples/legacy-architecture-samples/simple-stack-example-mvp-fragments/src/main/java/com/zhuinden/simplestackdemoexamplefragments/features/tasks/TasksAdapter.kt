package com.zhuinden.simplestackdemoexamplefragments.features.tasks

import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.data.models.Task
import com.zhuinden.simplestackdemoexamplefragments.util.Preconditions.checkNotNull
import com.zhuinden.simplestackdemoexamplefragments.util.inflate
import com.zhuinden.simplestackdemoexamplefragments.util.onClick
import kotlinx.android.extensions.LayoutContainer
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

        private val rowClickListener = View.OnClickListener { _ -> itemListener.onTaskRowClicked(task) }

        private val context = containerView.context

        private val title = containerView.findViewById<TextView>(R.id.title)
        private val complete = containerView.findViewById<CheckBox>(R.id.complete)

        init {
            containerView.setOnClickListener(rowClickListener)
            containerView.findViewById<View>(R.id.complete).onClick {
                itemListener.onTaskCheckClicked(task)
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
        fun onTaskRowClicked(task: Task)

        fun onTaskCheckClicked(task: Task)
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
