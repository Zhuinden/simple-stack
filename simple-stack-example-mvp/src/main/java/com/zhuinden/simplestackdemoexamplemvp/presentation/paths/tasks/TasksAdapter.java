package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task;

import java.util.List;

import static com.zhuinden.simplestackdemoexamplemvp.util.Preconditions.checkNotNull;

/**
 * Created by Zhuinden on 2017.01.26..
 */

public class TasksAdapter
        extends BaseAdapter { // FIXME: Why on earth does a 9 months old example still use ListView?
    public interface TaskItemListener {
        void onTaskClick(Task clickedTask);

        void onCompleteTaskClick(Task completedTask);

        void onActivateTaskClick(Task activatedTask);
    }

    private List<Task> mTasks;
    private TaskItemListener mItemListener;

    public TasksAdapter(List<Task> tasks, TaskItemListener itemListener) {
        setList(tasks);
        mItemListener = itemListener;
    }

    public void replaceData(List<Task> tasks) {
        setList(tasks);
        notifyDataSetChanged();
    }

    private void setList(List<Task> tasks) {
        mTasks = checkNotNull(tasks);
    }

    @Override
    public int getCount() {
        return mTasks.size();
    }

    @Override
    public Task getItem(int i) {
        return mTasks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View rowView = convertView;
        if(rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            rowView = inflater.inflate(R.layout.task_item, viewGroup, false); // TODO: viewholder pattern... or recycler view
        }

        final Task task = getItem(i);

        TextView titleTV = (TextView) rowView.findViewById(R.id.title);
        titleTV.setText(task.getTitleForList());

        CheckBox completeCB = (CheckBox) rowView.findViewById(R.id.complete);

        // Active/completed task UI
        completeCB.setChecked(task.isCompleted());
        if(task.isCompleted()) {
            rowView.setBackgroundDrawable(viewGroup.getContext().getResources().getDrawable(R.drawable.list_completed_touch_feedback));
        } else {
            rowView.setBackgroundDrawable(viewGroup.getContext().getResources().getDrawable(R.drawable.touch_feedback));
        }

        completeCB.setOnClickListener(v -> {
            if(!task.isCompleted()) {
                mItemListener.onCompleteTaskClick(task);
            } else {
                mItemListener.onActivateTaskClick(task);
            }
        });
        rowView.setOnClickListener(view1 -> mItemListener.onTaskClick(task));
        return rowView;
    }
}
