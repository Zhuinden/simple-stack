package com.example.stackmasterdetailfrag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.simplestack.KeyContextWrapper;

import butterknife.Unbinder;

/**
 * Created by Zhuinden on 2017.02.12..
 */

public class ViewHostFragment
        extends Fragment {
    public static final String KEY_TAG = "PATH";

    private Paths.Path key;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        key = getArguments().getParcelable(KEY_TAG);
        if(key == null) {
            throw new IllegalStateException("The fragment was initialized without a KEY argument!");
        }

        View view = LayoutInflater.from(new KeyContextWrapper(inflater.getContext(), key)).inflate(key.layout(), container, false);
        return view;
    }

    public final <K extends Paths.Path> K getKey() {
        // noinspection unchecked
        return (K) key;
    }
}