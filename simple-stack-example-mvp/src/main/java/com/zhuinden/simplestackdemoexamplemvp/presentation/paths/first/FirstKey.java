package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.first;

import android.view.View;

import com.google.auto.value.AutoValue;
import com.squareup.coordinators.Coordinator;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.application.Key;
import com.zhuinden.simplestackdemoexamplemvp.application.injection.SingletonComponent;

/**
 * Created by Owner on 2017. 01. 12..
 */
@AutoValue
public abstract class FirstKey implements Key {
    public static FirstKey create() {
        return new AutoValue_FirstKey(R.layout.path_first);
    }

    @Override
    public final Coordinator newCoordinator(SingletonComponent singletonComponent) {
        return singletonComponent.firstCoordinator();
    }

    @Override
    public int menu() {
        return R.menu.empty_menu;
    }

    @Override
    public boolean isFabVisible() {
        return false;
    }

    @Override
    public int navigationViewId() {
        return 0;
    }

    @Override
    public boolean shouldShowUp() {
        return false;
    }

    @Override
    public View.OnClickListener fabClickListener(View view) {
        return v -> {
        };
    }

    @Override
    public int fabDrawableIcon() {
        return 0;
    }
}
