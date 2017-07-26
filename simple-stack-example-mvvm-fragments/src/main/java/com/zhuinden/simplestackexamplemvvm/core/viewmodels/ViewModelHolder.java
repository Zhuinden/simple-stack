package com.zhuinden.simplestackexamplemvvm.core.viewmodels;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.zhuinden.simplestack.Bundleable;

import static com.zhuinden.simplestackexamplemvvm.util.Preconditions.checkNotNull;

/**
 * Created by Owner on 2017. 07. 26..
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
