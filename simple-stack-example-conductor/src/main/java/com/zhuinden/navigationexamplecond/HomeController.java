package com.zhuinden.navigationexamplecond;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.statebundle.StateBundle;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by Owner on 2017. 06. 29..
 */

public class HomeController
        extends BaseController {
    @OnClick(R.id.home_button)
    public void goToOtherView(View view) {
        MainActivity.get(view.getContext()).navigateTo(OtherKey.create());
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.home_view, container, false);
        ButterKnife.bind(this, view);
        HomeKey homeKey = getKey();
        return view;
    }
}
