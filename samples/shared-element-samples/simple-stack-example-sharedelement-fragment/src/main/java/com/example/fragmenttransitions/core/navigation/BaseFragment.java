package com.example.fragmenttransitions.core.navigation;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;

import com.zhuinden.simplestackextensions.fragments.KeyedFragment;

/**
 * Created by Zhuinden on 2020. 12. 18..
 */

public class BaseFragment
        extends KeyedFragment {
    public BaseFragment() {
    }

    public BaseFragment(@LayoutRes int layoutRes) {
        super(layoutRes);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startPostponedEnterTransition();
    }
}
