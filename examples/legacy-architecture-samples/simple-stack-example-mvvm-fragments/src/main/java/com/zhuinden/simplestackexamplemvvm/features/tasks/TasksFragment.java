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

package com.zhuinden.simplestackexamplemvvm.features.tasks;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.zhuinden.simplestackexamplemvvm.R;
import com.zhuinden.simplestackexamplemvvm.application.BaseFragment;
import com.zhuinden.simplestackexamplemvvm.application.injection.Injector;
import com.zhuinden.simplestackexamplemvvm.data.Task;
import com.zhuinden.simplestackexamplemvvm.databinding.TaskItemBinding;
import com.zhuinden.simplestackexamplemvvm.databinding.TasksFragmentBinding;
import com.zhuinden.simplestackexamplemvvm.util.ScrollChildSwipeRefreshLayout;
import com.zhuinden.simplestackexamplemvvm.util.SnackbarUtils;
import com.zhuinden.simplestackexamplemvvm.util.Strings;

import java.util.ArrayList;
import java.util.List;

import static com.zhuinden.simplestackexamplemvvm.util.Preconditions.checkNotNull;

/**
 * Display a grid of {@link Task}s. User can choose to view all, active or completed tasks.
 */
public class TasksFragment
        extends BaseFragment<TasksViewModel> {
    private TasksViewModel tasksViewModel;
    private TasksFragmentBinding viewBinding;
    private TasksAdapter adapter;
    private Observable.OnPropertyChangedCallback snackbarCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            if(!Strings.isNullOrEmpty(tasksViewModel.getSnackbarText())) {
                SnackbarUtils.showSnackbar(getView(), tasksViewModel.getSnackbarText());
            }
        }
    };
    private Observable.OnPropertyChangedCallback fabCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            View fab = getActivity().findViewById(R.id.fab);
            boolean visible = tasksViewModel.tasksAddViewVisible.get();
            fab.setVisibility(visible ? View.VISIBLE : View.GONE); // TODO: why doesn't this work?
        }
    };

    public TasksFragment() {
        // Requires empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewBinding = TasksFragmentBinding.inflate(inflater, container, false);
        viewBinding.setView(this);
        checkNotNull(tasksViewModel);
        viewBinding.setViewmodel(tasksViewModel);
        setHasOptionsMenu(true);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tasksViewModel.snackbarText.addOnPropertyChangedCallback(snackbarCallback);
        setupListAdapter();
        setupRefreshLayout();
    }

    @Override
    public void onStart() {
        super.onStart();
        tasksViewModel.start();
    }

    @Override
    public void onStop() {
        tasksViewModel.stop();
        super.onStop();
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
                tasksViewModel.refresh();
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu);
    }

    @Override
    public void onDestroyView() {
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout = viewBinding.refreshLayout;
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.destroyDrawingCache();
        swipeRefreshLayout.clearAnimation();
        tasksViewModel.snackbarText.removeOnPropertyChangedCallback(snackbarCallback);
        tasksViewModel.tasksAddViewVisible.removeOnPropertyChangedCallback(fabCallback);
        super.onDestroyView();
    }

    private void showFilteringPopUpMenu() {
        PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.filter_tasks, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
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
            tasksViewModel.reloadTasks();
            return true;
        });

        popup.show();
    }

    private void setupListAdapter() {
        ListView listView = viewBinding.tasksList;
        adapter = new TasksAdapter(new ArrayList<>(0), tasksViewModel);
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

    public void addNewTask() {
        tasksViewModel.addNewTask();
    }

    @Override
    public void bindViewModel(TasksViewModel viewModel) {
        checkNotNull(viewModel);
        if(this.tasksViewModel == viewModel) {
            return;
        }

        this.tasksViewModel = viewModel;
    }

    public static class TasksAdapter
            extends BaseAdapter {
        private final TasksViewModel tasksViewModel;

        private List<Task> tasks;

        public TasksAdapter(List<Task> tasks, TasksViewModel tasksViewModel) {
            this.tasksViewModel = tasksViewModel;
            setList(tasks);
        }

        public void replaceData(List<Task> tasks) {
            setList(tasks);
        }

        @Override
        public int getCount() {
            return tasks != null ? tasks.size() : 0;
        }

        @Override
        public Task getItem(int i) {
            return tasks.get(i);
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
                // Create the binding
                binding = TaskItemBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
            } else {
                // Recycling view
                binding = DataBindingUtil.getBinding(view);
            }

            final TaskItemViewModel taskItemViewModel = Injector.get().taskItemViewModel();

            binding.setViewmodel(taskItemViewModel);
            // To save on PropertyChangedCallbacks, wire the item's snackbar text observable to the
            // fragment's.
            taskItemViewModel.snackbarText.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable observable, int i) {
                    tasksViewModel.snackbarText.set(taskItemViewModel.getSnackbarText());
                }
            });
            taskItemViewModel.setTask(task);
            return binding.getRoot();
        }


        private void setList(List<Task> tasks) {
            this.tasks = tasks;
            notifyDataSetChanged();
        }
    }
}
