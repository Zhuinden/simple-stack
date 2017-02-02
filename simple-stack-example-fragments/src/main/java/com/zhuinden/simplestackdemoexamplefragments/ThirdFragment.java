package com.zhuinden.simplestackdemoexamplefragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhuinden on 2017.02.01..
 */

public class ThirdFragment
        extends Fragment {
    public static final String ARGUMENT_TEXT = "param";

    public static ThirdFragment create(String param) {
        ThirdFragment thirdFragment = new ThirdFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT_TEXT, param);
        thirdFragment.setArguments(bundle);
        return thirdFragment;
    }

    @BindView(R.id.third_textview)
    TextView textView;

    @OnClick(R.id.third_button_go)
    public void goToFourth() {
        BackstackService.getBackstack(getContext()).goTo(FourthKey.create());
    }

    @OnClick(R.id.third_button_clear)
    public void clearToFourth() {
        BackstackService.getBackstack(getContext()).setHistory(HistoryBuilder.single(FourthKey.create()), StateChange.FORWARD);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.path_third, container, false);
        ButterKnife.bind(this, view);
        textView.setText(getArguments().getString(ARGUMENT_TEXT, ""));
        return view;
    }
}