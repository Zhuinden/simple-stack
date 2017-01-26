package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.application.MainActivity;
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask.AddOrEditTaskKey;
import com.zhuinden.simplestackdemoexamplemvp.util.ScrollChildSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhuinden.simplestackdemoexamplemvp.util.Preconditions.checkNotNull;

/**
 * Created by Owner on 2017. 01. 26..
 */

public class TasksView
        extends ScrollChildSwipeRefreshLayout
        implements MainActivity.OptionsItemSelectedListener, StateChanger {
    public TasksView(Context context) {
        super(context);
    }

    public TasksView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @OnClick(R.id.noTasksAdd)
    public void onClickNoTasksAdd() {
        Backstack.get(getContext()).goTo(AddOrEditTaskKey.create());
    }

    @BindView(R.id.noTasks)
    View mNoTasksView;

    @BindView(R.id.noTasksIcon)
    ImageView mNoTaskIcon;

    @BindView(R.id.noTasksMain)
    TextView mNoTaskMainView;

    @BindView(R.id.noTasksAdd)
    TextView mNoTaskAddView;

    @BindView(R.id.tasksLL)
    LinearLayout mTasksView;

    @BindView(R.id.filteringLabel)
    TextView mFilteringLabelView;

    @BindView(R.id.tasks_list)
    ListView listView;

    TasksAdapter tasksAdapter;

    TasksContract.Presenter tasksPresenter;

    TaskItemListener taskItemListener = new TaskItemListener() {
        @Override
        public void onTaskClick(Task clickedTask) {
            tasksPresenter.openTaskDetails(clickedTask);
        }

        @Override
        public void onCompleteTaskClick(Task completedTask) {
            tasksPresenter.completeTask(completedTask);
        }

        @Override
        public void onActivateTaskClick(Task activatedTask) {
            tasksPresenter.activateTask(activatedTask);
        }
    };

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        tasksPresenter = new TasksContract.Presenter() { // TODO
            @Override
            public void result(int requestCode, int resultCode) {

            }

            @Override
            public void loadTasks(boolean forceUpdate) {

            }

            @Override
            public void addNewTask() {

            }

            @Override
            public void openTaskDetails(@NonNull Task requestedTask) {

            }

            @Override
            public void completeTask(@NonNull Task completedTask) {

            }

            @Override
            public void activateTask(@NonNull Task activeTask) {

            }

            @Override
            public void clearCompletedTasks() {

            }

            @Override
            public void setFiltering(TasksFilterType requestType) {

            }

            @Override
            public TasksFilterType getFiltering() {
                return null;
            }
        };
        tasksAdapter = new TasksAdapter(new ArrayList<>(0), taskItemListener);
        listView.setAdapter(tasksAdapter);
    }
    public void showFilteringPopUpMenu() {
        PopupMenu popup = new PopupMenu(getContext(), MainActivity.get(getContext()).findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.filter_tasks, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.active:
                    tasksPresenter.setFiltering(TasksFilterType.ACTIVE_TASKS);
                    break;
                case R.id.completed:
                    tasksPresenter.setFiltering(TasksFilterType.COMPLETED_TASKS);
                    break;
                default:
                    tasksPresenter.setFiltering(TasksFilterType.ALL_TASKS);
                    break;
            }
            tasksPresenter.loadTasks(false);
            return true;
        });

        popup.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                return true;
            case R.id.menu_clear:
                // TODO: CLEAR
                return true;
            case R.id.menu_refresh:
                // TODO: REFRESH
                return true;
            default:
        }
        return false;
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        // hack fix from  http://stackoverflow.com/a/27073879/2413303 to fix view staying on screen
        setRefreshing(false);
        destroyDrawingCache();
        clearAnimation();
        // end
        completionCallback.stateChangeComplete();
    }

    private static class TasksAdapter extends BaseAdapter { // FIXME: Why on earth does a 9 months old example still use ListView?
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
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.task_item, viewGroup, false); // TODO: viewholder pattern... or recycler view
            }

            final Task task = getItem(i);

            TextView titleTV = (TextView) rowView.findViewById(R.id.title);
            titleTV.setText(task.getTitleForList());

            CheckBox completeCB = (CheckBox) rowView.findViewById(R.id.complete);

            // Active/completed task UI
            completeCB.setChecked(task.isCompleted());
            if (task.isCompleted()) {
                rowView.setBackgroundDrawable(viewGroup.getContext()
                        .getResources().getDrawable(R.drawable.list_completed_touch_feedback));
            } else {
                rowView.setBackgroundDrawable(viewGroup.getContext()
                        .getResources().getDrawable(R.drawable.touch_feedback));
            }

            completeCB.setOnClickListener(v -> {
                if (!task.isCompleted()) {
                    mItemListener.onCompleteTaskClick(task);
                } else {
                    mItemListener.onActivateTaskClick(task);
                }
            });
            rowView.setOnClickListener(view1 -> mItemListener.onTaskClick(task));
            return rowView;
        }
    }

    public interface TaskItemListener {
        void onTaskClick(Task clickedTask);
        void onCompleteTaskClick(Task completedTask);
        void onActivateTaskClick(Task activatedTask);
    }
}
