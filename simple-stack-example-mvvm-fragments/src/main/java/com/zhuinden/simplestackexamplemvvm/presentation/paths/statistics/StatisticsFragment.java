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

package com.zhuinden.simplestackexamplemvvm.presentation.paths.statistics;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.simplestackexamplemvvm.R;
import com.zhuinden.simplestackexamplemvvm.application.BaseFragment;
import com.zhuinden.simplestackexamplemvvm.databinding.StatisticsFragmentBinding;

import static com.zhuinden.simplestackexamplemvvm.util.Preconditions.checkNotNull;

/**
 * Main UI for the statistics screen.
 */
public class StatisticsFragment
        extends BaseFragment<StatisticsViewModel> {

    private StatisticsFragmentBinding viewDataBinding;

    private StatisticsViewModel statisticsViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewDataBinding = DataBindingUtil.inflate(inflater, R.layout.statistics_fragment, container, false);
        setViewModelForView();
        return viewDataBinding.getRoot();
    }

    private void setViewModelForView() {
        if(viewDataBinding != null && statisticsViewModel != null) {
            viewDataBinding.setStats(statisticsViewModel);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        statisticsViewModel.start();
    }

    @Override
    public void onStop() {
        statisticsViewModel.stop();
        super.onStop();
    }

    @Override
    public void bindViewModel(StatisticsViewModel viewModel) {
        checkNotNull(viewModel);
        if(this.statisticsViewModel == viewModel) {
            return;
        }

        this.statisticsViewModel = viewModel;
        setViewModelForView();
    }
}
