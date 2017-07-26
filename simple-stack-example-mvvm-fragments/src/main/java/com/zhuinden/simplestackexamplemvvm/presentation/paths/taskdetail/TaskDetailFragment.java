/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.zhuinden.simplestackexamplemvvm.presentation.paths.taskdetail;

import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.simplestackexamplemvvm.R;
import com.zhuinden.simplestackexamplemvvm.application.BaseFragment;
import com.zhuinden.simplestackexamplemvvm.databinding.TaskdetailFragmentBinding;
import com.zhuinden.simplestackexamplemvvm.util.SnackbarUtils;

import static com.zhuinden.simplestackexamplemvvm.util.Preconditions.checkNotNull;


/**
 * Main UI for the task detail screen.
 */
public class TaskDetailFragment
        extends BaseFragment<TaskDetailViewModel> {
    private TaskDetailViewModel viewModel;
    private Observable.OnPropertyChangedCallback snackbarCallback;

    private void setupSnackbar() {
        snackbarCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                SnackbarUtils.showSnackbar(getView(), viewModel.getSnackbarText());
            }
        };
        viewModel.snackbarText.addOnPropertyChangedCallback(snackbarCallback);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.taskdetail_fragment, container, false);

        TaskdetailFragmentBinding viewDataBinding = TaskdetailFragmentBinding.bind(view);
        viewDataBinding.setViewmodel(viewModel);

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.start(this.<TaskDetailKey>getKey().taskId());
    }

    @Override
    public void onDestroy() {
        if(snackbarCallback != null) {
            viewModel.snackbarText.removeOnPropertyChangedCallback(snackbarCallback);
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_delete:
                viewModel.deleteTask();
                return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.taskdetail_fragment_menu, menu);
    }

    public void startEditTask() {
        viewModel.startEditTask();
    }

    @Override
    public void bindViewModel(TaskDetailViewModel viewModel) {
        checkNotNull(viewModel);
        this.viewModel = viewModel;
        setupSnackbar();
    }
}
