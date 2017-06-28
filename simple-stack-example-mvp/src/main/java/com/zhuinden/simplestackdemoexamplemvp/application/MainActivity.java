package com.zhuinden.simplestackdemoexamplemvp.application;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestack.navigator.DefaultStateChanger;
import com.zhuinden.simplestack.navigator.Navigator;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.data.manager.DatabaseManager;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks.TasksKey;
import com.zhuinden.simplestackdemoexamplemvp.util.BackstackHolder;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity
        extends AppCompatActivity
        implements StateChanger, DefaultStateChanger.ViewChangeCompletionListener {
    @BindView(R.id.drawer_layout)
    MainView mainView;

    @BindView(R.id.root)
    RelativeLayout root;

    public interface OptionsItemSelectedListener {
        boolean onOptionsItemSelected(MenuItem menuItem);
    }

    public static final String TAG = "MainActivity";

    public static MainActivity get(Context context) {
        // noinspection ResourceType
        return (MainActivity) context.getSystemService(TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mainView.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return mainView.onCreateOptionsMenu(menu);
    }

    @Inject
    DatabaseManager databaseManager;

    @Inject
    BackstackHolder backstackHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CustomApplication.get(this).initialize();
        CustomApplication.get(this).getComponent().inject(this);
        databaseManager.init(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Backstack backstack = Navigator.configure()
                .setDeferredInitialization(true)
                .setStateChanger(DefaultStateChanger.configure()
                        .setExternalStateChanger(this)
                        .setViewChangeCompletionListener(this)
                        .create(this, root))
                .install(this, root, HistoryBuilder.single(TasksKey.create()));
        backstackHolder.setBackstack(backstack);

        MainScopeListener mainScopeListener = (MainScopeListener) getSupportFragmentManager().findFragmentByTag(
                "MAIN_SCOPE_LISTENER");
        if(mainScopeListener == null) {
            mainScopeListener = new MainScopeListener();
            getSupportFragmentManager().beginTransaction().add(mainScopeListener, "MAIN_SCOPE_LISTENER").commit();
        }

        mainView.onCreate();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Navigator.executeDeferredInitialization(this);
        mainView.onPostCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mainView.onConfigChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if(mainView.onBackPressed()) {
            return;
        }
        if(!Navigator.onBackPressed(this)) {
            super.onBackPressed();
        }
    }

    @Override
    public Object getSystemService(String name) {
        if(TAG.equals(name)) {
            return this;
        }
        return super.getSystemService(name);
    }

    @Override
    public void handleStateChange(@NonNull StateChange stateChange, @NonNull StateChanger.Callback completionCallback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            completionCallback.stateChangeComplete();
            return;
        }
        mainView.handleStateChange(stateChange, () -> {
        });
        completionCallback.stateChangeComplete();
    }

    @Override
    public void handleViewChangeComplete(@NonNull StateChange stateChange, //
                                         @NonNull ViewGroup container, //
                                         @Nullable View previousView, //
                                         @NonNull View newView, //
                                         @NonNull DefaultStateChanger.ViewChangeCompletionListener.Callback completionCallback) {
        mainView.setupViewsForKey(Backstack.getKey(newView.getContext()), newView);
        completionCallback.viewChangeComplete();
    }
}
