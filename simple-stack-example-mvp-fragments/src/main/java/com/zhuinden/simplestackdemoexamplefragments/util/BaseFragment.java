package com.zhuinden.simplestackdemoexamplefragments.util;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.simplestack.KeyContextWrapper;
import com.zhuinden.simplestackdemoexamplefragments.application.Key;

/**
 * Created by Zhuinden on 2017.01.26..
 */

public abstract class BaseFragment<V extends BaseViewContract, P extends BasePresenter<V>>
        extends Fragment {
    public static final String KEY_TAG = "KEY";

    public abstract P getPresenter();

    public abstract V getThis();

    private Key key;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(getKey().menu(), menu);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            getPresenter().fromBundle(savedInstanceState.getParcelable("PRESENTER_STATE"));
        }
        key = getArguments().getParcelable(KEY_TAG);
        if(key == null) {
            throw new IllegalStateException("The view was initialized without a KEY argument!");
        }
        return LayoutInflater.from(new KeyContextWrapper(inflater.getContext(), key)).inflate(key.layout(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPresenter().attachFragment(getThis());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("PRESENTER_STATE", getPresenter().toBundle());
    }

    @Override
    public void onDestroyView() {
        getPresenter().detachFragment(getThis());
        super.onDestroyView();
    }

    public <K extends Key> K getKey() {
        // noinspection unchecked
        return (K) key;
    }
}
