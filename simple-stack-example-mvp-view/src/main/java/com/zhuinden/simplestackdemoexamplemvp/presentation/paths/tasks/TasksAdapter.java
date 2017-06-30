package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhuinden.simplestackdemoexamplemvp.util.Preconditions.checkNotNull;

/**
 * Created by Zhuinden on 2017.01.26..
 */

public class TasksAdapter
        extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {
    public static class TaskViewHolder
            extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;

        @BindView(R.id.complete)
        CheckBox complete;

        @OnClick(R.id.complete)
        public void toggleComplete() {
            if(!task.isCompleted()) {
                itemListener.completeTask(task);
            } else {
                itemListener.uncompleteTask(task);
            }
        }

        Task task;

        TaskItemListener itemListener;

        View.OnClickListener rowClickListener = (view) -> {
            itemListener.openTask(task);
        };

        View row;

        Context context;

        public TaskViewHolder(View itemView, TaskItemListener itemListener) {
            super(itemView);
            context = itemView.getContext();
            this.itemListener = itemListener;
            this.row = itemView;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(rowClickListener);
        }

        public void bind(Task task) {
            this.task = task;
            title.setText(task.getTitleForList());
            complete.setChecked(task.isCompleted());
            if(task.isCompleted()) {
                row.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_completed_touch_feedback));
            } else {
                row.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.touch_feedback));
            }
        }
    }

    public interface TaskItemListener {
        void openTask(Task task);

        void completeTask(Task task);

        void uncompleteTask(Task task);
    }

    private List<Task> tasks;
    private TaskItemListener itemListener;

    public TasksAdapter(List<Task> tasks, TaskItemListener itemListener) {
        setData(tasks);
        this.itemListener = itemListener;
        notifyDataSetChanged();
    }

    public List<Task> getData() {
        return Collections.unmodifiableList(tasks);
    }

    public void setData(List<Task> tasks) {
        this.tasks = checkNotNull(tasks);
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TaskViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false), itemListener);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        final Task task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
