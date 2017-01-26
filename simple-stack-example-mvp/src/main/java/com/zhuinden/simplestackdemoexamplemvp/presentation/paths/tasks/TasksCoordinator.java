package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxrelay.BehaviorRelay;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.application.MainActivity;
import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository;
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask.AddOrEditTaskKey;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.taskdetail.TaskDetailKey;
import com.zhuinden.simplestackdemoexamplemvp.util.BaseCoordinator;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Owner on 2017. 01. 26..
 */
// UNSCOPED!
public class TasksCoordinator
        extends BaseCoordinator<TasksView>
        implements Bundleable {
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
    TextView filterLabel;

    @BindView(R.id.tasks_list)
    RecyclerView listView;

    @Inject
    TaskRepository taskRepository;

    TasksAdapter tasksAdapter;

    TasksView tasksView;

    BehaviorRelay<TasksFilterType> filterType = BehaviorRelay.create(TasksFilterType.ALL_TASKS);

    Subscription subscription;

    Unbinder unbinder;

    TasksAdapter.TaskItemListener taskItemListener = new TasksAdapter.TaskItemListener() {
        @Override
        public void openTask(Task task) {
            openTaskDetails(task);
        }

        @Override
        public void completeTask(Task task) {
            TasksCoordinator.this.completeTask(task);
        }

        @Override
        public void uncompleteTask(Task task) {
            TasksCoordinator.this.uncompleteTask(task);
        }
    };

    private void uncompleteTask(Task task) {
        taskRepository.insertTask(task.toBuilder().setCompleted(false).build());
    }

    private void completeTask(Task task) {
        taskRepository.insertTask(task.toBuilder().setCompleted(true).build());
    }

    private void openTaskDetails(Task clickedTask) {
        backstack.goTo(TaskDetailKey.create(clickedTask.id()));
    }

    @Override
    public void attachView(TasksView view) {
        backstack = Backstack.get(view.getContext());
        tasksView = view;
        unbinder = ButterKnife.bind(this, view);
        tasksAdapter = new TasksAdapter(new ArrayList<>(0), taskItemListener);
        listView.setAdapter(tasksAdapter);
        listView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        subscription = filterType.asObservable() //
                .doOnNext(tasksFilterType -> filterLabel.setText(tasksFilterType.getFilterText())) //
                .switchMap((tasksFilterType -> tasksFilterType.filterTask(taskRepository))) //
                .observeOn(Schedulers.computation())
                .map(tasks -> Pair.create(DiffUtil.calculateDiff(new TaskDiffCallback(tasksAdapter.getData(), tasks)), tasks))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pairOfDiffResultAndTasks -> {
            if(tasksAdapter != null) {
                DiffUtil.DiffResult diffResult = pairOfDiffResultAndTasks.first;
                List<Task> tasks = pairOfDiffResultAndTasks.second;
                tasksAdapter.setData(tasks);
                diffResult.dispatchUpdatesTo(tasksAdapter);
                if(tasks.isEmpty()) {
                    filterType.getValue().showEmptyViews(this);
                } else {
                    hideEmptyViews();
                }
            }
        });
    }

    public void hideEmptyViews() {
        mTasksView.setVisibility(View.VISIBLE);
        mNoTasksView.setVisibility(View.GONE);
    }

    static class TaskDiffCallback
            extends DiffUtil.Callback {
        private List<Task> oldTasks;
        private List<Task> newTasks;

        public TaskDiffCallback(List<Task> oldTasks, List<Task> newTasks) {
            this.oldTasks = oldTasks;
            this.newTasks = newTasks;
        }

        @Override
        public int getOldListSize() {
            return oldTasks == null ? 0 : oldTasks.size();
        }

        @Override
        public int getNewListSize() {
            return newTasks.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return newTasks.get(newItemPosition).id().equals(oldTasks.get(oldItemPosition).id());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return newTasks.get(newItemPosition).equals(oldTasks.get(oldItemPosition));
        }
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
            //loadTasks(false); // reactive data source ftw
            return true;
        });

        popup.show();
    }

    private void setFiltering(TasksFilterType filterType) {
        this.filterType.call(filterType);
    }

    public void clear() {
        // TODO
    }

    public void refresh() {
        // TODO
    }

    public void showNoActiveTasks() {
        showNoTasksViews(tasksView.getContext().getResources().getString(R.string.no_tasks_active), R.drawable.ic_check_circle_24dp, false);
    }

    public void showNoTasks() {
        showNoTasksViews(tasksView.getContext().getResources().getString(R.string.no_tasks_all),
                R.drawable.ic_assignment_turned_in_24dp,
                false);
    }

    public void showNoCompletedTasks() {
        showNoTasksViews(tasksView.getContext().getResources().getString(R.string.no_tasks_completed),
                R.drawable.ic_verified_user_24dp,
                false);
    }

    private void showNoTasksViews(String mainText, int iconRes, boolean showAddView) {
        mTasksView.setVisibility(View.GONE);
        mNoTasksView.setVisibility(View.VISIBLE);

        mNoTaskMainView.setText(mainText);
        mNoTaskIcon.setImageDrawable(tasksView.getContext().getResources().getDrawable(iconRes));
        mNoTaskAddView.setVisibility(showAddView ? View.VISIBLE : View.GONE);
    }

    @Override
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("FILTERING", filterType.getValue().name());
        return bundle;
    }

    @Override
    public void fromBundle(@Nullable Bundle bundle) {
        if(bundle != null) {
            filterType.call(TasksFilterType.valueOf(bundle.getString("FILTERING")));
        }
    }
}
