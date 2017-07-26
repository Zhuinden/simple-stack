/*
 * Copyright 2017 Gabor Varadi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhuinden.simplestackexamplemvvm.core.viewmodels;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.zhuinden.simplestack.Bundleable;

import static com.zhuinden.simplestackexamplemvvm.util.Preconditions.checkNotNull;

/**
 * This retained fragment holds a ViewModel, and persists/restores its state if implements Bundleable.
 */
public class ViewModelHolder
        extends Fragment {
    public ViewModelHolder() {
        setRetainInstance(true);
    }

    private Object viewModel;

    private Bundle savedInstanceState;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
    }

    public <T> T getViewModel() {
        // noinspection unchecked
        return (T) viewModel;
    }

    public <T> void setViewModel(@NonNull T viewModel) {
        checkNotNull(viewModel);
        this.viewModel = viewModel;
        if(savedInstanceState != null) {
            if(viewModel instanceof Bundleable) {
                ((Bundleable) viewModel).fromBundle(savedInstanceState.getParcelable("VIEWMODEL_STATE"));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(viewModel != null && viewModel instanceof Bundleable) {
            outState.putParcelable("VIEWMODEL_STATE", ((Bundleable) viewModel).toBundle());
        }
    }
}
