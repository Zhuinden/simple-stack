package com.zhuinden.navigationexamplefrag.application;

import android.os.Bundle;

import com.zhuinden.navigationexamplefrag.R;
import com.zhuinden.navigationexamplefrag.databinding.ActivityMainBinding;
import com.zhuinden.navigationexamplefrag.screens.DashboardKey;
import com.zhuinden.navigationexamplefrag.screens.HomeKey;
import com.zhuinden.navigationexamplefrag.screens.NotificationKey;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.SimpleStateChanger;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.navigator.Navigator;
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity
        extends AppCompatActivity
        implements SimpleStateChanger.NavigationHandler {
    private static final String TAG = "MainActivity";

    DefaultFragmentStateChanger fragmentStateChanger;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.navigation.setOnNavigationItemSelectedListener(item -> {
            Backstack backstack = Navigator.getBackstack(this);
            switch(item.getItemId()) {
                case R.id.navigation_home:
                    backstack.setHistory(History.of(HomeKey.create()), StateChange.REPLACE);
                    return true;
                case R.id.navigation_dashboard:
                    backstack.setHistory(History.of(DashboardKey.create()), StateChange.REPLACE);
                    return true;
                case R.id.navigation_notifications:
                    backstack.setHistory(History.of(NotificationKey.create()), StateChange.REPLACE);
                    return true;
            }
            return false;
        });

        fragmentStateChanger = new DefaultFragmentStateChanger(getSupportFragmentManager(),
                                                               R.id.container);

        Navigator.configure()
                .setStateChanger(new SimpleStateChanger(this))
                .install(this, binding.container, History.single(HomeKey.create()));
    }

    @Override
    public void onBackPressed() {
        if(!Navigator.onBackPressed(this)) {
            super.onBackPressed();
        }
    }

    @Override
    public void onNavigationEvent(@NonNull StateChange stateChange) {
        fragmentStateChanger.handleStateChange(stateChange);
    }
}