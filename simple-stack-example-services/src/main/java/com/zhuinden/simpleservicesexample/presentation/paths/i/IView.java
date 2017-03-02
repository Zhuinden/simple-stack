package com.zhuinden.simpleservicesexample.presentation.paths.i;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.zhuinden.simpleservicesexample.R;
import com.zhuinden.simpleservicesexample.presentation.paths.j.J;
import com.zhuinden.simpleservicesexample.utils.Preconditions;
import com.zhuinden.simpleservicesexample.utils.ServiceLocator;
import com.zhuinden.simpleservicesexample.utils.StackService;
import com.zhuinden.simplestack.Backstack;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Owner on 2017. 02. 17..
 */

public class IView
        extends RelativeLayout {
    public IView(Context context) {
        super(context);
    }

    public IView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public IView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @OnClick(R.id.i_button)
    public void click() {
        StackService.get(getContext()).goTo(J.create(Backstack.getKey(getContext())));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
//        Preconditions.checkNotNull(ServiceLocator.getService(getContext(), "H"), "Service should not be null");
        Preconditions.checkNotNull(ServiceLocator.getService(getContext(), "I"), "Service should not be null");
    }
}
