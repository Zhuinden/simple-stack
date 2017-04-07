package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.application.Injector;
import com.zhuinden.simplestackdemoexamplemvp.application.MainActivity;
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task;
import com.zhuinden.simplestackdemoexamplemvp.util.MessageQueue;
import com.zhuinden.simplestackdemoexamplemvp.util.ScrollChildSwipeRefreshLayout;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.statebundle.StateBundle;

import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Single;

/**
 * Created by Owner on 2017. 01. 26..
 */

public class TasksView
        extends ScrollChildSwipeRefreshLayout
        implements MainActivity.OptionsItemSelectedListener, StateChanger, Bundleable, MessageQueue.Receiver {
    public TasksView(Context context) {
        super(context);
        init(context);
    }

    public TasksView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        if(!isInEditMode()) {
            Injector.get().inject(this);
        }
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

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        // hack fix from  http://stackoverflow.com/a/27073879/2413303 to fix view staying on screen
        setRefreshing(false);
        destroyDrawingCache();
        clearAnimation();
        // end
        completionCallback.stateChangeComplete();
    }

    @Override
    public StateBundle toBundle() {
        return tasksPresenter.toBundle();
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {
        if(bundle != null) {
            tasksPresenter.fromBundle(bundle);
        }
    }

    @Inject
    TasksPresenter tasksPresenter;

    @Inject
    Resources resources;

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

    TasksAdapter tasksAdapter;

    public static class SavedSuccessfullyMessage {
    }

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
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        tasksAdapter = new TasksAdapter(new ArrayList<>(0), taskItemListener);
        listView.setAdapter(tasksAdapter);
        listView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        setColorSchemeColors(ContextCompat.getColor(this.getContext(),
                R.color.colorPrimary),
                ContextCompat.getColor(this.getContext(), R.color.colorAccent),
                ContextCompat.getColor(this.getContext(), R.color.colorPrimaryDark));
        // Set the scrolling view in the custom SwipeRefreshLayout.
        setScrollUpChild(listView);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        tasksPresenter.attachView(this);
        setOnRefreshListener(this::refresh);
    }

    @Override
    protected void onDetachedFromWindow() {
        setOnRefreshListener(null);
        tasksPresenter.detachView(this);
        super.onDetachedFromWindow();
    }

    @Override
    public void receiveMessage(Object message) {
        if(message instanceof TasksView.SavedSuccessfullyMessage) {
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
        PopupMenu popup = new PopupMenu(this.getContext(),
                MainActivity.get(this.getContext()).findViewById(R.id.menu_filter));
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
        ((TasksView) this).setRefreshing(true);
        Single.just("").delay(2500, TimeUnit.MILLISECONDS).subscribe(ignored -> { // TODO: do something useful
            if(this != null) {
                ((TasksView) this).setRefreshing(false);
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
        Snackbar.make(this, message, Snackbar.LENGTH_LONG).show();
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

}
