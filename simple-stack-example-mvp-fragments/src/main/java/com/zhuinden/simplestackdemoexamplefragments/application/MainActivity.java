package com.zhuinden.simplestackdemoexamplefragments.application;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestackdemoexamplefragments.R;
import com.zhuinden.simplestackdemoexamplefragments.data.manager.DatabaseManager;
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.tasks.TasksKey;
import com.zhuinden.simplestackdemoexamplefragments.util.BackstackHolder;
import com.zhuinden.simplestackdemoexamplefragments.util.FragmentStateChanger;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity
        extends AppCompatActivity
        implements StateChanger {
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

    BackstackDelegate backstackDelegate;
    FragmentStateChanger fragmentStateChanger;

    @Inject
    DatabaseManager databaseManager;

    @Inject
    BackstackHolder backstackHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CustomApplication.get(this).initialize();
        CustomApplication.get(this).getComponent().inject(this);
        databaseManager.init(this);

        backstackDelegate = new BackstackDelegate(null /* delayed init */);
        backstackDelegate.onCreate(savedInstanceState, //
                getLastCustomNonConfigurationInstance(), //
                HistoryBuilder.single(TasksKey.create()));

        backstackHolder.setBackstack(backstackDelegate.getBackstack());

        super.onCreate(savedInstanceState);

        MainScopeListener mainScopeListener = (MainScopeListener) getSupportFragmentManager().findFragmentByTag("MAIN_SCOPE_LISTENER");
        if(mainScopeListener == null) {
            mainScopeListener = new MainScopeListener();
            getSupportFragmentManager().beginTransaction().add(mainScopeListener, "MAIN_SCOPE_LISTENER").commit();
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        this.fragmentStateChanger = new FragmentStateChanger(getSupportFragmentManager(), R.id.root);
        mainView.onCreate();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        backstackDelegate.setStateChanger(this);
        mainView.onPostCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mainView.onConfigChanged(newConfig);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return backstackDelegate.onRetainCustomNonConfigurationInstance();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        backstackDelegate.onPostResume();
    }

    @Override
    public void onBackPressed() {
        if(mainView.onBackPressed()) {
            return;
        }
        if(!backstackDelegate.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        backstackDelegate.onPause();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        backstackDelegate.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        backstackDelegate.onDestroy();
        super.onDestroy();
    }

    @Override
    public Object getSystemService(String name) {
        if(TAG.equals(name)) {
            return this;
        }
        return super.getSystemService(name);
    }

    @Override
    public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            // no-op
            completionCallback.stateChangeComplete();
            return;
        }

        fragmentStateChanger.handleStateChange(stateChange);

        mainView.setupViewsForKey(stateChange.topNewState());
        completionCallback.stateChangeComplete();
    }
}
