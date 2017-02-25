package com.zhuinden.simplestackdemonestedstack.application;

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
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestackdemonestedstack.R;
import com.zhuinden.simplestackdemonestedstack.application.Key;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.main.chromecast.ChromeCastKey;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.main.cloudsync.CloudSyncKey;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.main.list.ListKey;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.main.mail.MailKey;
import com.zhuinden.simplestackdemonestedstack.util.BackPressListener;
import com.zhuinden.simplestackdemonestedstack.util.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class MainActivity
        extends AppCompatActivity
        implements StateChanger {
    private static final String TAG = "MainActivity";

    public static MainActivity get(Context context) {
        //noinspection ResourceType
        return (MainActivity)context.getSystemService(TAG);
    }

    @BindView(R.id.root)
    RelativeLayout root;

    @BindView(R.id.coordinator_root)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.bottom_navigation)
    BottomNavigation bottomNavigation;

    public enum StackType {
        CLOUDSYNC {
            @Override
            public Key getKey() {
                return CloudSyncKey.create();
            }
        },
        CHROMECAST {
            @Override
            public Key getKey() {
                return ChromeCastKey.create();
            }
        },
        MAIL {
            @Override
            public Key getKey() {
                return MailKey.create();
            }
        },
        LIST {
            @Override
            public Key getKey() {
                return ListKey.create();
            }
        };
        
        public abstract Key getKey();
    }
    
    BackstackDelegate backstackDelegate;
    Backstack backstack;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        backstackDelegate = BackstackDelegate.create();
        backstackDelegate.onCreate(savedInstanceState, getLastCustomNonConfigurationInstance(), HistoryBuilder.single(StackType.values()[0].getKey()));
        backstack = backstackDelegate.getBackstack();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        bottomNavigation.setOnMenuItemClickListener(new BottomNavigation.OnMenuItemSelectionListener() {
            @Override
            public void onMenuItemSelect(@IdRes int menuItemId, int itemIndex, boolean b) {
                Key previousKey = Backstack.getKey(root.getChildAt(0).getContext());
                StackType previousStack = StackType.valueOf(previousKey.stackIdentifier());
                StackType newStack = StackType.values()[itemIndex];
                Key newKey = newStack.getKey();
                int direction = previousStack.ordinal() < newStack.ordinal() ? StateChange.FORWARD : previousStack.ordinal() > newStack.ordinal() ? StateChange.BACKWARD : StateChange.REPLACE;
                backstack.setHistory(HistoryBuilder.from(backstack).removeLast().add(newKey).build(), direction);
            }

            @Override
            public void onMenuItemReselect(@IdRes int menuItemId, int itemIndex, boolean b) {

            }
        });
        backstackDelegate.setStateChanger(this);
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
        View currentChild = root.getChildAt(0);
        if(currentChild != null && currentChild instanceof BackPressListener) {
            boolean handled = ((BackPressListener) currentChild).onBackPressed();
            if(handled) {
                return;
            }
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
        if(MainActivity.TAG.equals(name)) {
            return this;
        }
        return super.getSystemService(name);
    }

    private void exchangeViewForKey(Key newKey, int direction) {
        backstackDelegate.persistViewToState(root.getChildAt(0));
        Context newContext = backstackDelegate.createContext(this, newKey);
        View previousView = root.getChildAt(0);
        View newView = LayoutInflater.from(newContext).inflate(newKey.layout(), root, false);
        backstackDelegate.restoreViewFromState(newView);
        root.addView(newView);

        if(direction == StateChange.REPLACE) {
            finishStateChange(previousView);
        } else {
            ViewUtils.waitForMeasure(newView, (view, width, height) -> {
                runAnimation(previousView, newView, direction, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        finishStateChange(previousView);
                    }
                });
            });
        }
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
            Key newKey = stateChange.topNewState();
            StackType newStack = StackType.valueOf(newKey.stackIdentifier());
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