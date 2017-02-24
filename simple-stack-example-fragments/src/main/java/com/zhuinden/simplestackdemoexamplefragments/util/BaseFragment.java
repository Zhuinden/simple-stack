package com.zhuinden.simplestackdemoexamplefragments.util;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.simplestackdemoexamplefragments.application.Key;
import com.zhuinden.simplestackdemoexamplefragments.application.MainActivity;

import butterknife.Unbinder;

/**
 * Created by Zhuinden on 2017.01.26..
 */

public abstract class BaseFragment<C extends BaseFragment<C, P>, P extends BasePresenter<C, P>>
        extends Fragment {
    public static final String KEY_TAG = "KEY";

    public abstract P getPresenter();

    public abstract C getThis();

    private Key key;

    private Unbinder unbinder;

    protected abstract Unbinder bindViews(View view);

    protected abstract void injectSelf();

    public BaseFragment() {
        injectSelf();
    }

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            getPresenter().fromBundle(savedInstanceState.getParcelable("PRESENTER_STATE"));
        }
        key = getArguments().getParcelable(KEY_TAG);
        if(key == null) {
            throw new IllegalStateException("The fragment was initialized without a KEY argument!");
        }
        View view = LayoutInflater.from(MainActivity.getDelegate(getContext()).createContext(inflater.getContext(), key)).inflate(key.layout(), container, false);
        unbinder = bindViews(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPresenter().attachFragment(getThis());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("PRESENTER_STATE", getPresenter().toBundle());
    }

    @Override
    public void onDestroyView() {
        getPresenter().detachFragment(getThis());
        unbinder.unbind();
        unbinder = null;
        super.onDestroyView();
    }

    public <K extends Key> K getKey() {
        // noinspection unchecked
        return (K) key;
    }
}
