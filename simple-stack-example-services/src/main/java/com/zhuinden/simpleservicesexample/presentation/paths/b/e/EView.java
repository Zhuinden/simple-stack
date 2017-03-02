package com.zhuinden.simpleservicesexample.presentation.paths.b.e;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.zhuinden.simpleservicesexample.R;
import com.zhuinden.simpleservicesexample.presentation.paths.h.H;
import com.zhuinden.simpleservicesexample.utils.Preconditions;
import com.zhuinden.simpleservicesexample.utils.ServiceLocator;
import com.zhuinden.simpleservicesexample.utils.StackService;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhuinden on 2017.02.14..
 */

public class EView
        extends RelativeLayout {
    public EView(Context context) {
        super(context);
    }

    public EView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public EView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @OnClick(R.id.e_button)
    public void click(View view) {
        StackService.get(getContext()).goTo(H.create());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
//        Preconditions.checkNotNull(ServiceLocator.getService(getContext(), "A"), "Service should not be null");
        Preconditions.checkNotNull(ServiceLocator.getService(getContext(), "B"), "Service should not be null");
        Preconditions.checkNotNull(ServiceLocator.getService(getContext(), "E"), "Service should not be null");
    }
}
