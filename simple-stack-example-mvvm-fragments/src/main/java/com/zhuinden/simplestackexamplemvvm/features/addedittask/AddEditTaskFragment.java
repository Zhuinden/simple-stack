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

package com.zhuinden.simplestackexamplemvvm.features.addedittask;

import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.simplestackexamplemvvm.R;
import com.zhuinden.simplestackexamplemvvm.application.BaseFragment;
import com.zhuinden.simplestackexamplemvvm.databinding.AddtaskFragmentBinding;
import com.zhuinden.simplestackexamplemvvm.util.SnackbarUtils;

import static com.zhuinden.simplestackexamplemvvm.util.Preconditions.checkNotNull;

/**
 * Main UI for the add task screen. Users can enter a task title and description.
 */
public class AddEditTaskFragment
        extends BaseFragment<AddEditTaskViewModel> {
    private AddEditTaskViewModel viewModel;
    private AddtaskFragmentBinding viewDataBinding;
    private Observable.OnPropertyChangedCallback snackbarCallback;

    public AddEditTaskFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.addtask_fragment, container, false);
        if(viewDataBinding == null) {
            viewDataBinding = AddtaskFragmentBinding.bind(root);
        }

        viewDataBinding.setViewmodel(viewModel);

        setHasOptionsMenu(true);
        return viewDataBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.start(this.<AddEditTaskKey>getKey().taskId());
    }

    @Override
    public void onStop() {
        viewModel.stop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if(snackbarCallback != null) {
            viewModel.snackbarText.removeOnPropertyChangedCallback(snackbarCallback);
        }
        super.onDestroy();
    }

    public void saveTask() {
        viewModel.saveTask();
    }

    @Override
    public void bindViewModel(AddEditTaskViewModel viewModel) {
        checkNotNull(viewModel);
        if(this.viewModel == viewModel) {
            return;
        }
        this.viewModel = viewModel;
        snackbarCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                SnackbarUtils.showSnackbar(getView(), viewModel.getSnackbarText());
            }
        };
        viewModel.snackbarText.addOnPropertyChangedCallback(snackbarCallback);
    }
}
