package com.zhuinden.navigationexamplefrag.screens;

import android.os.Bundle;
import android.view.View;

import com.zhuinden.navigationexamplefrag.R;
import com.zhuinden.simplestack.navigator.Navigator;
import com.zhuinden.simplestackextensions.fragments.KeyedFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Owner on 2017. 06. 29..
 */

public class HomeFragment
        extends KeyedFragment {
    public HomeFragment() {
        super(R.layout.home_view);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.home_button).setOnClickListener(v -> {
            Navigator.getBackstack(requireActivity()).goTo(OtherKey.create());
        });

        HomeKey homeKey = getKey(); // get args
    }
}
