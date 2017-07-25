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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.NavigationView;
import android.support.test.espresso.IdlingResource;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.application.Injection;
import com.example.android.architecture.blueprints.todoapp.presentation.paths.addedittask.AddEditTaskActivity;
import com.example.android.architecture.blueprints.todoapp.presentation.paths.statistics.StatisticsActivity;
import com.example.android.architecture.blueprints.todoapp.presentation.paths.taskdetail.TaskDetailActivity;
import com.example.android.architecture.blueprints.todoapp.util.ActivityUtils;
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource;


public class TasksActivity
        extends AppCompatActivity
        implements TaskItemNavigator, TasksNavigator {

    private DrawerLayout drawerLayout;

    private TasksViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_act);

        setupToolbar();

        setupNavigationDrawer();

        TasksFragment tasksFragment = findOrCreateViewFragment();

        viewModel = findOrCreateViewModel();
        viewModel.setNavigator(this);

        // Link View and ViewModel
        tasksFragment.setViewModel(viewModel);
    }

    @Override
    protected void onDestroy() {
        viewModel.onActivityDestroyed();
        super.onDestroy();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return viewModel;
    }

    private TasksViewModel findOrCreateViewModel() {
        TasksViewModel retainedViewModel = (TasksViewModel) getLastCustomNonConfigurationInstance();

        if(retainedViewModel != null) {
            // If the model was retained, return it.
            return retainedViewModel;
        } else {
            // There is no ViewModel yet, create it.
            return Injection.get().tasksViewModel();
        }
    }

    @NonNull
    private TasksFragment findOrCreateViewFragment() {
        TasksFragment tasksFragment = (TasksFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(tasksFragment == null) {
            // Create the fragment
            tasksFragment = TasksFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), tasksFragment, R.id.contentFrame);
        }
        return tasksFragment;
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(navigationView != null) {
            setupDrawerContent(navigationView);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.list_navigation_menu_item:
                        // Do nothing, we're already on that screen
                        break;
                    case R.id.statistics_navigation_menu_item:
                        Intent intent = new Intent(TasksActivity.this, StatisticsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                // Close the navigation drawer when an item is selected.
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        viewModel.handleActivityResult(requestCode, resultCode);
    }

    @Override
    public void openTaskDetails(String taskId) {
        Intent intent = new Intent(this, TaskDetailActivity.class);
        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, taskId);
        startActivityForResult(intent, AddEditTaskActivity.REQUEST_CODE);

    }

    @Override
    public void addNewTask() {
        Intent intent = new Intent(this, AddEditTaskActivity.class);
        startActivityForResult(intent, AddEditTaskActivity.REQUEST_CODE);
    }
}
