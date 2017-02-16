package com.zhuinden.simplestackdemoexamplemvp.application;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.squareup.coordinators.Coordinator;
import com.squareup.coordinators.CoordinatorProvider;
import com.squareup.coordinators.Coordinators;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.data.manager.DatabaseManager;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks.TasksKey;
import com.zhuinden.simplestackdemoexamplemvp.util.BackstackHolder;
import com.zhuinden.simplestackdemoexamplemvp.util.ViewUtils;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mainView.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return mainView.onCreateOptionsMenu(menu);
    }

    BackstackDelegate backstackDelegate;

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

        backstackDelegate = new BackstackDelegate(null /* delayed init */);
        backstackDelegate.onCreate(savedInstanceState, //
                getLastCustomNonConfigurationInstance(), //
                HistoryBuilder.single(TasksKey.create()));

        backstackHolder.setBackstack(backstackDelegate.getBackstack());

        MainScopeListener mainScopeListener = (MainScopeListener) getSupportFragmentManager().findFragmentByTag("MAIN_SCOPE_LISTENER");
        if(mainScopeListener == null) {
            mainScopeListener = new MainScopeListener();
            getSupportFragmentManager().beginTransaction().add(mainScopeListener, "MAIN_SCOPE_LISTENER").commit();
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mainView.onCreate();

        Coordinators.installBinder(root, new CoordinatorProvider() {
            @Nullable
            @Override
            public Coordinator provideCoordinator(View view) {
                Key key = Backstack.getKey(view.getContext());
                return key.newCoordinator(CustomApplication.get(view.getContext()).getComponent());
            }
        });
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
        backstackDelegate.persistViewToState(root.getChildAt(0));
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
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            completionCallback.stateChangeComplete();
            return;
        }
        mainView.handleStateChange(stateChange, () -> {
        });

        final View previousView = root.getChildAt(0);
        backstackDelegate.persistViewToState(previousView);

        Key newKey = stateChange.topNewState();
        Context newContext = stateChange.createContext(this, newKey);
        View newView = LayoutInflater.from(newContext).inflate(newKey.layout(), root, false);

        root.addView(newView);
        backstackDelegate.restoreViewFromState(newView);

        if(stateChange.getDirection() == StateChange.REPLACE) {
            finishStateChange(previousView, newView, completionCallback);
        } else {
            ViewUtils.waitForMeasure(newView, (view, width, height) -> {
                runAnimation(previousView, newView, stateChange.getDirection(), new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        finishStateChange(previousView, newView, completionCallback);
                    }
                });
            });
        }
    }

    private void finishStateChange(View previousView, View newView, Callback completionCallback) {
        root.removeView(previousView);
        mainView.setupViewsForKey(Backstack.getKey(newView.getContext()), newView);
        completionCallback.stateChangeComplete();
    }

    // animation
    private void runAnimation(final View previousView, final View newView, int direction, AnimatorListenerAdapter animatorListenerAdapter) {
        Animator animator = createSegue(previousView, newView, direction);
        animator.addListener(animatorListenerAdapter);
        animator.start();
    }

    private Animator createSegue(View from, View to, int direction) {
        boolean backward = direction == StateChange.BACKWARD;
        int fromTranslation = backward ? from.getWidth() : -from.getWidth();
        int toTranslation = backward ? -to.getWidth() : to.getWidth();

        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(from, View.TRANSLATION_X, fromTranslation));
        set.play(ObjectAnimator.ofFloat(to, View.TRANSLATION_X, toTranslation, 0));
        return set;
    }
}
