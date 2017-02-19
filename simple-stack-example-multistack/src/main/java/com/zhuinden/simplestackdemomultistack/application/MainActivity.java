package com.zhuinden.simplestackdemomultistack.application;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.KeyContextWrapper;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestackdemomultistack.R;
import com.zhuinden.simplestackdemomultistack.presentation.paths.main.chromecast.ChromeCastKey;
import com.zhuinden.simplestackdemomultistack.presentation.paths.main.cloudsync.CloudSyncKey;
import com.zhuinden.simplestackdemomultistack.presentation.paths.main.list.ListKey;
import com.zhuinden.simplestackdemomultistack.presentation.paths.main.mail.MailKey;
import com.zhuinden.simplestackdemomultistack.util.Multistack;
import com.zhuinden.simplestackdemomultistack.util.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

import static com.zhuinden.simplestackdemomultistack.application.MainActivity.StackType.CHROMECAST;
import static com.zhuinden.simplestackdemomultistack.application.MainActivity.StackType.CLOUDSYNC;
import static com.zhuinden.simplestackdemomultistack.application.MainActivity.StackType.LIST;
import static com.zhuinden.simplestackdemomultistack.application.MainActivity.StackType.MAIL;

public class MainActivity
        extends AppCompatActivity
        implements StateChanger {
    public enum StackType {
        CLOUDSYNC,
        CHROMECAST,
        MAIL,
        LIST;
    }

    @BindView(R.id.root)
    RelativeLayout root;

    @BindView(R.id.coordinator_root)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.bottom_navigation)
    BottomNavigation bottomNavigation;

    Multistack multistack;

    private boolean isAnimating; // unfortunately, we must manually ensure that you can't navigate while you're animating.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.multistack = new Multistack();

        multistack.add(CLOUDSYNC.name(), new BackstackDelegate(null));
        multistack.add(CHROMECAST.name(), new BackstackDelegate(null));
        multistack.add(MAIL.name(), new BackstackDelegate(null));
        multistack.add(LIST.name(), new BackstackDelegate(null));

        Multistack.NonConfigurationInstance nonConfigurationInstance = (Multistack.NonConfigurationInstance) getLastCustomNonConfigurationInstance();

        multistack.onCreate(savedInstanceState);

        multistack.onCreate(CLOUDSYNC.name(), savedInstanceState, nonConfigurationInstance, CloudSyncKey.create());
        multistack.onCreate(CHROMECAST.name(), savedInstanceState, nonConfigurationInstance, ChromeCastKey.create());
        multistack.onCreate(MAIL.name(), savedInstanceState, nonConfigurationInstance, MailKey.create());
        multistack.onCreate(LIST.name(), savedInstanceState, nonConfigurationInstance, ListKey.create());

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        bottomNavigation.setOnMenuItemClickListener(new BottomNavigation.OnMenuItemSelectionListener() {
            @Override
            public void onMenuItemSelect(@IdRes int menuItemId, int itemIndex, boolean b) {
                multistack.setSelectedStack(StackType.values()[itemIndex].name());
            }

            @Override
            public void onMenuItemReselect(@IdRes int menuItemId, int itemIndex, boolean b) {

            }
        });
        multistack.setStateChanger(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return multistack.onRetainCustomNonConfigurationInstance();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        multistack.onPostResume();
    }

    @Override
    public void onBackPressed() {
        if(!multistack.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        multistack.onPause();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        multistack.persistViewToState(root.getChildAt(0));
        multistack.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        multistack.onDestroy();
        super.onDestroy();
    }

    @Override
    public Object getSystemService(String name) {
        if(multistack != null) {
            BackstackDelegate stack = multistack.get(name);
            if(stack != null) {
                return stack;
            }
        }
        return super.getSystemService(name);
    }

    private void exchangeViewForKey(Key newKey, int direction) {
        multistack.persistViewToState(root.getChildAt(0));
        multistack.setSelectedStack(newKey.stackIdentifier());
        Context newContext = new KeyContextWrapper(this, newKey);
        View previousView = root.getChildAt(0);
        View newView = LayoutInflater.from(newContext).inflate(newKey.layout(), root, false);
        multistack.restoreViewFromState(newView);
        root.addView(newView);

        if(direction == StateChange.REPLACE) {
            finishStateChange(previousView);
        } else {
            isAnimating = true;
            ViewUtils.waitForMeasure(newView, (view, width, height) -> {
                runAnimation(previousView, newView, direction, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isAnimating = false;
                        finishStateChange(previousView);
                    }
                });
            });
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return !isAnimating && super.dispatchTouchEvent(ev); // unfortunately, we must manually make sure you can't navigate while you're animating.
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            // no-op
            completionCallback.stateChangeComplete();
            return;
        }
        int direction = StateChange.REPLACE;
        if(root.getChildAt(0) != null) {
            Key previousKey = Backstack.getKey(root.getChildAt(0).getContext());
            StackType previousStack = StackType.valueOf(previousKey.stackIdentifier());
            StackType newStack = StackType.valueOf(((Key) stateChange.topNewState()).stackIdentifier());
            direction = previousStack.ordinal() < newStack.ordinal() ? StateChange.FORWARD : previousStack.ordinal() > newStack.ordinal() ? StateChange.BACKWARD : StateChange.REPLACE;
        }
        exchangeViewForKey(stateChange.topNewState(), direction);
        completionCallback.stateChangeComplete();
    }

    private void finishStateChange(View previousView) {
        root.removeView(previousView);
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