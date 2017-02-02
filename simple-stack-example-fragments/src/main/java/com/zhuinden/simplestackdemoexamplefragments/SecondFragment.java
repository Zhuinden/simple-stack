package com.zhuinden.simplestackdemoexamplefragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhuinden on 2017.02.01..
 */

public class SecondFragment extends Fragment {
    @BindView(R.id.second_edittext)
    EditText editText;

    @OnClick(R.id.second_go_to_third)
    public void goToThird() {
        BackstackService.getBackstack(getContext()).goTo(ThirdKey.create(editText.getText().toString()));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.path_second, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
