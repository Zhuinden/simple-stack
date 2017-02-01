package com.zhuinden.simplestackdemoexamplefragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhuinden on 2017.02.01..
 */

public class ThirdFragment
        extends Fragment {
    @OnClick(R.id.third_button)
    public void goToFourth() {
        BackstackService.getBackstack(getContext()).setHistory(HistoryBuilder.single(FourthKey.create()), StateChange.Direction.FORWARD);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.path_third, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}