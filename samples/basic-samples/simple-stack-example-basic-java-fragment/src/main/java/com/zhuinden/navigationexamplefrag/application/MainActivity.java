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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import kotlin.Suppress;

public class MainActivity
    extends AppCompatActivity
    implements SimpleStateChanger.NavigationHandler {
    private static final String TAG = "MainActivity";

    DefaultFragmentStateChanger fragmentStateChanger;

    private ActivityMainBinding binding;

    @SuppressWarnings("DEPRECATION")
    private final OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if(!Navigator.onBackPressed(MainActivity.this)) {
                this.remove();
                onBackPressed();  // this is the only safe way to manually invoke onBackPressed when using onBackPressedDispatcher`
                MainActivity.this.getOnBackPressedDispatcher().addCallback(this);
            }
        }
    };

    @Override
    public final void onBackPressed() { // you cannot use `onBackPressed()` if you use `OnBackPressedDispatcher`
        super.onBackPressed();
    }

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

        getOnBackPressedDispatcher().addCallback(backPressedCallback); // this is required for `onBackPressedDispatcher` to work correctly

        Navigator.configure()
            .setStateChanger(new SimpleStateChanger(this))
            .install(this, binding.container, History.single(HomeKey.create()));
    }

    @Override
    public void onNavigationEvent(@NonNull StateChange stateChange) {
        fragmentStateChanger.handleStateChange(stateChange);
    }
}