package com.zhuinden.simplestackdemomultistack.application;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.KeyChange;
import com.zhuinden.simplestack.KeyChanger;
import com.zhuinden.simplestack.KeyContextWrapper;
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
        implements KeyChanger {
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

        multistack.add(CLOUDSYNC.name(), new BackstackDelegate());
        multistack.add(CHROMECAST.name(), new BackstackDelegate());
        multistack.add(MAIL.name(), new BackstackDelegate());
        multistack.add(LIST.name(), new BackstackDelegate());

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
        multistack.setKeyChanger(this);
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

        if(direction == KeyChange.REPLACE) {
            finishKeyChange(previousView);
        } else {
            isAnimating = true;
            ViewUtils.waitForMeasure(newView, (view, width, height) -> {
                runAnimation(previousView, newView, direction, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isAnimating = false;
                        finishKeyChange(previousView);
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
    public void handleKeyChange(@NonNull KeyChange keyChange, @NonNull Callback completionCallback) {
        if(keyChange.isTopNewKeyEqualToPrevious()) {
            // no-op
            completionCallback.keyChangeComplete();
            return;
        }
        int direction = KeyChange.REPLACE;
        if(root.getChildAt(0) != null) {
            Key previousKey = Backstack.getKey(root.getChildAt(0).getContext());
            StackType previousStack = StackType.valueOf(previousKey.stackIdentifier());
            StackType newStack = StackType.valueOf(((Key) keyChange.topNewKey()).stackIdentifier());
            direction = previousStack.ordinal() < newStack.ordinal() ? KeyChange.FORWARD : previousStack.ordinal() > newStack.ordinal() ? KeyChange.BACKWARD : KeyChange.REPLACE;
        }
        exchangeViewForKey(keyChange.topNewKey(), direction);
        completionCallback.keyChangeComplete();
    }

    private void finishKeyChange(View previousView) {
        root.removeView(previousView);
    }

    // animation
    private void runAnimation(final View previousView, final View newView, int direction, AnimatorListenerAdapter animatorListenerAdapter) {
        Animator animator = createSegue(previousView, newView, direction);
        animator.addListener(animatorListenerAdapter);
        animator.start();
    }

    private Animator createSegue(View from, View to, int direction) {
        boolean backward = direction == KeyChange.BACKWARD;
        int fromTranslation = backward ? from.getWidth() : -from.getWidth();
        int toTranslation = backward ? -to.getWidth() : to.getWidth();

        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(from, View.TRANSLATION_X, fromTranslation));
        set.play(ObjectAnimator.ofFloat(to, View.TRANSLATION_X, toTranslation, 0));
        return set;
    }
}