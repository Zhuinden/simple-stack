package com.zhuinden.navigationexampleview.application;

import android.os.Bundle;

import com.zhuinden.navigationexampleview.R;
import com.zhuinden.navigationexampleview.databinding.ActivityMainBinding;
import com.zhuinden.navigationexampleview.screens.DashboardKey;
import com.zhuinden.navigationexampleview.screens.HomeKey;
import com.zhuinden.navigationexampleview.screens.NotificationKey;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.navigator.Navigator;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity
        extends AppCompatActivity {
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

        Navigator.install(this, binding.container, History.single(HomeKey.create()));
    }

    @Override
    public void onBackPressed() {
        if(!Navigator.onBackPressed(this)) {
            super.onBackPressed();
        }
    }
}
