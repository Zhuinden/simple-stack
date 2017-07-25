/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.architecture.blueprints.todoapp.presentation.paths.tasks;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.ScrollChildSwipeRefreshLayout;
import com.example.android.architecture.blueprints.todoapp.application.BaseFragment;
import com.example.android.architecture.blueprints.todoapp.application.Injection;
import com.example.android.architecture.blueprints.todoapp.data.Task;
import com.example.android.architecture.blueprints.todoapp.databinding.TaskItemBinding;
import com.example.android.architecture.blueprints.todoapp.databinding.TasksFragBinding;
import com.example.android.architecture.blueprints.todoapp.util.SnackbarUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Display a grid of {@link Task}s. User can choose to view all, active or completed tasks.
 */
public class TasksFragment
        extends BaseFragment {

    private TasksViewModel tasksViewModel;

    private TasksFragBinding viewBinding;

    private TasksAdapter adapter;

    private Observable.OnPropertyChangedCallback snackbarCallback;

    public TasksFragment() {
        // Requires empty public constructor
    }

    public static TasksFragment newInstance() {
        return new TasksFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        tasksViewModel.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewBinding = TasksFragBinding.inflate(inflater, container, false);

        viewBinding.setView(this);

        viewBinding.setViewmodel(tasksViewModel);

        setHasOptionsMenu(true);

        View root = viewBinding.getRoot();

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_clear:
                tasksViewModel.clearCompletedTasks();
                break;
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                break;
            case R.id.menu_refresh:
                tasksViewModel.loadTasks(true);
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu);
    }

    public void setViewModel(TasksViewModel viewModel) {
        tasksViewModel = viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupSnackbar();

        setupFab();

        setupListAdapter();

        setupRefreshLayout();
    }

    @Override
    public void onDestroy() {
        adapter.onDestroy();
        if(snackbarCallback != null) {
            tasksViewModel.snackbarText.removeOnPropertyChangedCallback(snackbarCallback);
        }
        super.onDestroy();
    }

    private void setupSnackbar() {
        snackbarCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                SnackbarUtils.showSnackbar(getView(), tasksViewModel.getSnackbarText());
            }
        };
        tasksViewModel.snackbarText.addOnPropertyChangedCallback(snackbarCallback);
    }

    private void showFilteringPopUpMenu() {
        PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.filter_tasks, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.active:
                        tasksViewModel.setFiltering(TasksFilterType.ACTIVE_TASKS);
                        break;
                    case R.id.completed:
                        tasksViewModel.setFiltering(TasksFilterType.COMPLETED_TASKS);
                        break;
                    default:
                        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS);
                        break;
                }
                tasksViewModel.loadTasks(false);
                return true;
            }
        });

        popup.show();
    }

    private void setupFab() {
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_add_task);

        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tasksViewModel.addNewTask();
            }
        });
    }

    private void setupListAdapter() {
        ListView listView = viewBinding.tasksList;

        adapter = new TasksAdapter(new ArrayList<>(0), (TasksActivity) getActivity(), tasksViewModel);
        listView.setAdapter(adapter);
    }

    private void setupRefreshLayout() {
        ListView listView = viewBinding.tasksList;
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout = viewBinding.refreshLayout;
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(listView);
    }

    public static class TasksAdapter
            extends BaseAdapter {

        @Nullable
        private TaskItemNavigator mTaskItemNavigator;

        private final TasksViewModel mTasksViewModel;

        private List<Task> mTasks;

        public TasksAdapter(List<Task> tasks, TasksActivity taskItemNavigator, TasksViewModel tasksViewModel) {
            mTaskItemNavigator = taskItemNavigator;
            mTasksViewModel = tasksViewModel;
            setList(tasks);

        }

        public void onDestroy() {
            mTaskItemNavigator = null;
        }

        public void replaceData(List<Task> tasks) {
            setList(tasks);
        }

        @Override
        public int getCount() {
            return mTasks != null ? mTasks.size() : 0;
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            Task task = getItem(i);
            TaskItemBinding binding;
            if(view == null) {
                // Inflate
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

                // Create the binding
                binding = TaskItemBinding.inflate(inflater, viewGroup, false);
            } else {
                // Recycling view
                binding = DataBindingUtil.getBinding(view);
            }

            final TaskItemViewModel viewmodel = Injection.get().taskItemViewModel();

            viewmodel.setNavigator(mTaskItemNavigator);

            binding.setViewmodel(viewmodel);
            // To save on PropertyChangedCallbacks, wire the item's snackbar text observable to the
            // fragment's.
            viewmodel.snackbarText.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable observable, int i) {
                    mTasksViewModel.snackbarText.set(viewmodel.getSnackbarText());
                }
            });
            viewmodel.setTask(task);

            return binding.getRoot();
        }


        private void setList(List<Task> tasks) {
            mTasks = tasks;
            notifyDataSetChanged();
        }
    }
}
