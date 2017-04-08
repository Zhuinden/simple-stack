package com.zhuinden.simplestackdemonestedstack.presentation.paths.main;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestack.navigator.DefaultStateChanger;
import com.zhuinden.simplestackdemonestedstack.R;
import com.zhuinden.simplestackdemonestedstack.application.Key;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.main.chromecast.ChromeCastKey;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.main.cloudsync.CloudSyncKey;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.main.list.ListKey;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.main.mail.MailKey;
import com.zhuinden.simplestackdemonestedstack.util.NestSupportServiceManager;
import com.zhuinden.simplestackdemonestedstack.util.ServiceLocator;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

/**
 * Created by Zhuinden on 2017.02.26..
 */

public class MainView
        extends CoordinatorLayout
        implements StateChanger {
    @BindView(R.id.root)
    RelativeLayout root;

    @BindView(R.id.bottom_navigation)
    BottomNavigation bottomNavigation;

    DefaultStateChanger defaultStateChanger;

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

    public MainView(Context context) {
        super(context);
    }

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    BackstackManager backstackManager;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        defaultStateChanger = DefaultStateChanger.create(getContext(), root);

        bottomNavigation.setOnMenuItemClickListener(new BottomNavigation.OnMenuItemSelectionListener() {
            @Override
            public void onMenuItemSelect(@IdRes int menuItemId, int itemIndex, boolean b) {
                Key previousKey = Backstack.getKey(root.getChildAt(0).getContext());
                StackType newStack = StackType.values()[itemIndex];
                Key newKey = newStack.getKey();
                int direction = StateChange.REPLACE;
                StackType previousStack = StackType.valueOf(previousKey.stackIdentifier());
                direction = previousStack.ordinal() < newStack.ordinal() ? StateChange.FORWARD : previousStack.ordinal() > newStack.ordinal() ? StateChange.BACKWARD : StateChange.REPLACE;
                backstackManager.getBackstack().setHistory(HistoryBuilder.from(backstackManager).removeLast().add(newKey).build(), direction);
            }

            @Override
            public void onMenuItemReselect(@IdRes int menuItemId, int itemIndex, boolean b) {

            }
        });
        backstackManager = ServiceLocator.getService(getContext(), Key.NESTED_STACK);
        backstackManager.setStateChanger(this);
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        NestSupportServiceManager.get(getContext()).setupServices(stateChange, true);
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            // no-op
            completionCallback.stateChangeComplete();
            return;
        }
        int direction = StateChange.REPLACE;
        Key previousKey = stateChange.topPreviousState();
        Key newKey = stateChange.topNewState();
        if(root.getChildAt(0) != null) {
            StackType newStack = StackType.valueOf(newKey.stackIdentifier());
            if(previousKey != null) {
                StackType previousStack = StackType.valueOf(previousKey.stackIdentifier());
                direction = previousStack.ordinal() < newStack.ordinal() ? StateChange.FORWARD : previousStack.ordinal() > newStack.ordinal() ? StateChange.BACKWARD : StateChange.REPLACE;
            }
        }
        defaultStateChanger.performViewChange(previousKey, newKey, stateChange, direction, completionCallback);
    }
}
