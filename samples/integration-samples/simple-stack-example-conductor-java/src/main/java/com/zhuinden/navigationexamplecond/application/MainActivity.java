package com.zhuinden.navigationexamplecond.application;

import android.os.Bundle;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Router;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zhuinden.navigationexamplecond.R;
import com.zhuinden.navigationexamplecond.core.navigation.ControllerStateChanger;
import com.zhuinden.navigationexamplecond.screens.DashboardKey;
import com.zhuinden.navigationexamplecond.screens.HomeKey;
import com.zhuinden.navigationexamplecond.screens.NotificationKey;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.SimpleStateChanger;
import com.zhuinden.simplestack.StateChange;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity
        extends AppCompatActivity
        implements SimpleStateChanger.NavigationHandler {
    private static final String TAG = "MainActivity";

    private BackstackViewModel backstackViewModel;
    private Backstack backstack;

    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    @BindView(R.id.root)
    ViewGroup root;

    Router router;

    ControllerStateChanger controllerStateChanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        backstackViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                BackstackViewModel backstackViewModel = new BackstackViewModel();
                if(savedInstanceState != null) {
                    backstackViewModel.backstack.fromBundle(savedInstanceState.getParcelable("backstack"));
                }
                //noinspection unchecked
                return (T) backstackViewModel;
            }
        }).get(BackstackViewModel.class);
        backstack = backstackViewModel.backstack;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        navigation.setOnNavigationItemSelectedListener(item -> {
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
        router = Conductor.attachRouter(this, root, savedInstanceState);
        controllerStateChanger = new ControllerStateChanger(router);
        backstack.setStateChanger(new SimpleStateChanger(this));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        backstackViewModel.backstack.reattachStateChanger();
    }

    @Override
    protected void onPause() {
        backstackViewModel.backstack.detachStateChanger();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("backstack", backstack.toBundle());
    }

    @Override
    public void onBackPressed() {
        if(!backstack.goBack()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        backstack.executePendingStateChange();
        if(isFinishing()) {
            backstack.finalizeScopes();
        }
    }

    public Backstack getBackstack() {
        return backstack;
    }

    @Override
    public void onNavigationEvent(@Nonnull StateChange stateChange) {
        controllerStateChanger.handleStateChange(stateChange);
    }
}
