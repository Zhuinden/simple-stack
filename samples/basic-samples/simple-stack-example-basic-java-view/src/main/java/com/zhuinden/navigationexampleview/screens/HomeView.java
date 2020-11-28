package com.zhuinden.navigationexampleview.screens;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.zhuinden.navigationexampleview.R;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.navigator.Navigator;

/**
 * Created by Owner on 2017. 06. 29..
 */

public class HomeView
        extends RelativeLayout /*implements Bundleable*/ {
    public HomeView(Context context) {
        super(context);
    }

    public HomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public HomeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        findViewById(R.id.home_button).setOnClickListener(v -> {
            Navigator.getBackstack(getContext()).goTo(OtherKey.create());
        });

        HomeKey homeKey = Backstack.getKey(getContext()); // get args
    }
}
