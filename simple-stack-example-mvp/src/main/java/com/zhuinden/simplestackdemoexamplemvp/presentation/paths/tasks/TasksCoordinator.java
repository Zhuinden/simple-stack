package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks;

import android.support.v7.widget.PopupMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.application.MainActivity;
import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository;
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask.AddOrEditTaskKey;
import com.zhuinden.simplestackdemoexamplemvp.util.BaseCoordinator;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Owner on 2017. 01. 26..
 */
// UNSCOPED!
public class TasksCoordinator
        extends BaseCoordinator<TasksView> {
    TasksFilterType filterType;

    @Inject
    public TasksCoordinator() {
    }

    Backstack backstack;


    @OnClick(R.id.noTasksAdd)
    public void onClickNoTasksAdd() {
        backstack.goTo(AddOrEditTaskKey.create());
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

    @Inject
    TaskRepository taskRepository;

    TasksAdapter tasksAdapter;

    TasksView tasksView;

    Subscription subscription;

    Unbinder unbinder;

    TasksAdapter.TaskItemListener taskItemListener = new TasksAdapter.TaskItemListener() {
        @Override
        public void onTaskClick(Task clickedTask) {
            openTaskDetails(clickedTask);
        }

        @Override
        public void onCompleteTaskClick(Task completedTask) {
            completeTask(completedTask);
        }

        @Override
        public void onActivateTaskClick(Task activatedTask) {
            activateTask(activatedTask);
        }
    };

    private void activateTask(Task activatedTask) {

    }

    private void completeTask(Task completedTask) {

    }

    private void openTaskDetails(Task clickedTask) {

    }

    @Override
    public void attachView(TasksView view) {
        backstack = Backstack.get(view.getContext());
        tasksView = view;
        unbinder = ButterKnife.bind(this, view);
        tasksAdapter = new TasksAdapter(new ArrayList<>(0), taskItemListener);
        listView.setAdapter(tasksAdapter);
        subscription = taskRepository.getTasks().observeOn(AndroidSchedulers.mainThread()).subscribe(tasks -> {
            if(tasksAdapter != null) {
                tasksAdapter.replaceData(tasks);
            }
        });
    }

    @Override
    public void detachView(TasksView view) {
        subscription.unsubscribe();
        unbinder.unbind();
        tasksView = null;
    }


    public void showFilteringPopupMenu() {
        PopupMenu popup = new PopupMenu(tasksView.getContext(), MainActivity.get(tasksView.getContext()).findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.filter_tasks, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            switch(item.getItemId()) {
                case R.id.active:
                    setFiltering(TasksFilterType.ACTIVE_TASKS);
                    break;
                case R.id.completed:
                    setFiltering(TasksFilterType.COMPLETED_TASKS);
                    break;
                default:
                    setFiltering(TasksFilterType.ALL_TASKS);
                    break;
            }
            //loadTasks(false);
            return true;
        });

        popup.show();
    }

    private void setFiltering(TasksFilterType filterType) {
        this.filterType = filterType;
        // TODO: change result set in adapter
    }

    public void clear() {
        // TODO
    }

    public void refresh() {
        // TODO
    }
}
