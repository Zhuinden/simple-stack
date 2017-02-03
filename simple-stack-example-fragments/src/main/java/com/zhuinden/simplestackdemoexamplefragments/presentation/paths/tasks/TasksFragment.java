package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.tasks;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhuinden.simplestackdemoexamplefragments.R;
import com.zhuinden.simplestackdemoexamplefragments.application.Injector;
import com.zhuinden.simplestackdemoexamplefragments.application.MainActivity;
import com.zhuinden.simplestackdemoexamplefragments.presentation.objects.Task;
import com.zhuinden.simplestackdemoexamplefragments.util.BaseFragment;
import com.zhuinden.simplestackdemoexamplefragments.util.MessageQueue;
import com.zhuinden.simplestackdemoexamplefragments.util.ScrollChildSwipeRefreshLayout;

import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Single;

/**
 * Created by Owner on 2017. 01. 26..
 */
// UNSCOPED!
public class TasksFragment
        extends BaseFragment<TasksFragment, TasksPresenter>
        implements MessageQueue.Receiver {
    public static class SavedSuccessfullyMessage {
    }

    public TasksFragment() {
    }

    @Inject
    TasksPresenter tasksPresenter;

    @Inject
    Resources resources;

    @Inject
    MessageQueue messageQueue;

    @OnClick(R.id.noTasksAdd)
    void openAddNewTask() {
        tasksPresenter.openAddNewTask();
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

    @BindView(R.id.refresh_layout)
    ScrollChildSwipeRefreshLayout swipeRefreshLayout;

    TasksAdapter tasksAdapter;

    TasksAdapter.TaskItemListener taskItemListener = new TasksAdapter.TaskItemListener() {
        @Override
        public void openTask(Task task) {
            tasksPresenter.openTaskDetails(task);
        }

        @Override
        public void completeTask(Task task) {
            tasksPresenter.completeTask(task);
        }

        @Override
        public void uncompleteTask(Task task) {
            tasksPresenter.uncompleteTask(task);
        }
    };

    @Override
    public TasksPresenter getPresenter() {
        return tasksPresenter;
    }

    @Override
    public TasksFragment getThis() {
        return this;
    }

    @Override
    protected Unbinder bindViews(View view) {
        return ButterKnife.bind(this, view);
    }

    @Override
    protected void injectSelf() {
        Injector.get().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        tasksAdapter = new TasksAdapter(new ArrayList<>(0), taskItemListener);
        listView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        listView.setAdapter(tasksAdapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorPrimary),
                ContextCompat.getColor(getContext(), R.color.colorAccent),
                ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(listView);
        swipeRefreshLayout.setOnRefreshListener(this::refresh);

        messageQueue.requestMessages(getKey(), this);
    }

    @Override
    public void onDestroyView() {
        if(swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(null);
        }
        super.onDestroyView();
    }

    @Override
    public void receiveMessage(Object message) {
        if(message instanceof SavedSuccessfullyMessage) {
            showSuccessfullySavedMessage();
        }
    }

    public Pair<DiffUtil.DiffResult, List<Task>> calculateDiff(List<Task> tasks) {
        return Pair.with(DiffUtil.calculateDiff(new TasksDiffCallback(tasksAdapter.getData(), tasks)), tasks);
    }

    public void hideEmptyViews() {
        mTasksView.setVisibility(View.VISIBLE);
        mNoTasksView.setVisibility(View.GONE);
    }

    public void showTasks(Pair<DiffUtil.DiffResult, List<Task>> pairOfDiffResultAndTasks, TasksFilterType filterType) {
        if(tasksAdapter != null) {
            DiffUtil.DiffResult diffResult = pairOfDiffResultAndTasks.getValue0();
            List<Task> tasks = pairOfDiffResultAndTasks.getValue1();
            tasksAdapter.setData(tasks);
            diffResult.dispatchUpdatesTo(tasksAdapter);
            if(tasks.isEmpty()) {
                filterType.showEmptyViews(this);
            } else {
                hideEmptyViews();
            }
        }
    }

    public void showFilteringPopupMenu() {
        PopupMenu popup = new PopupMenu(getContext(), MainActivity.get(getContext()).findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.filter_tasks, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            switch(item.getItemId()) {
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
            //loadTasks(false); // reactive data source ftw
            return true;
        });

        popup.show();
    }

    public void clearCompletedTasks() {
        tasksPresenter.deleteCompletedTasks();
    }

    public void refresh() {
        swipeRefreshLayout.setRefreshing(true);
        Single.just("").delay(2500, TimeUnit.MILLISECONDS).subscribe(ignored -> { // TODO: do something useful
            if(swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void showNoActiveTasks() {
        showNoTasksViews(resources.getString(R.string.no_tasks_active), R.drawable.ic_check_circle_24dp, false);
    }

    public void showNoTasks() {
        showNoTasksViews(resources.getString(R.string.no_tasks_all), R.drawable.ic_assignment_turned_in_24dp, false);
    }

    public void showNoCompletedTasks() {
        showNoTasksViews(resources.getString(R.string.no_tasks_completed), R.drawable.ic_verified_user_24dp, false);
    }

    public void showTaskMarkedComplete() {
        showMessage(resources.getString(R.string.task_marked_complete));
    }

    public void showTaskMarkedActive() {
        showMessage(resources.getString(R.string.task_marked_active));
    }

    public void showCompletedTasksCleared() {
        showMessage(resources.getString(R.string.completed_tasks_cleared));
    }

    public void showLoadingTasksError() {
        showMessage(resources.getString(R.string.loading_tasks_error));
    }

    public void showSuccessfullySavedMessage() {
        showMessage(resources.getString(R.string.successfully_saved_task_message));
    }

    private void showMessage(String message) {
        if(getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }
    }

    private void showNoTasksViews(String mainText, int iconRes, boolean showAddView) {
        mTasksView.setVisibility(View.GONE);
        mNoTasksView.setVisibility(View.VISIBLE);

        mNoTaskMainView.setText(mainText);
        mNoTaskIcon.setImageDrawable(resources.getDrawable(iconRes));
        mNoTaskAddView.setVisibility(showAddView ? View.VISIBLE : View.GONE);
    }

    public void setFilterLabelText(int filterText) {
        filterLabel.setText(filterText);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.menu_filter:
                showFilteringPopupMenu();
                return true;
            case R.id.menu_clear:
                clearCompletedTasks();
                return true;
            case R.id.menu_refresh:
                refresh();
                return true;
            default:
        }
        return false;
    }
}
