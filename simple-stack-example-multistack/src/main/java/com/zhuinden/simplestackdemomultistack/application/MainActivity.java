package com.zhuinden.simplestackdemomultistack.application;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

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
import com.zhuinden.simplestackdemomultistack.util.ServiceLocator;

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
        CHROMECAST,
        CLOUDSYNC,
        LIST,
        MAIL;
    }

    @BindView(R.id.root)
    RelativeLayout root;

    @BindView(R.id.coordinator_root)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.bottom_navigation)
    BottomNavigation bottomNavigation;

    BackstackDelegate chromeCastStack;
    BackstackDelegate cloudSyncStack;
    BackstackDelegate listStack;
    BackstackDelegate mailStack;

    Multistack multistack;

    String currentStack = CHROMECAST.name();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.multistack = new Multistack();

        chromeCastStack = multistack.add(CHROMECAST.name(), new BackstackDelegate(null));
        cloudSyncStack = multistack.add(CLOUDSYNC.name(), new BackstackDelegate(null));
        listStack = multistack.add(LIST.name(), new BackstackDelegate(null));
        mailStack = multistack.add(MAIL.name(), new BackstackDelegate(null));

        if(savedInstanceState != null) {
            currentStack = savedInstanceState.getString("currentStack", CHROMECAST.name());
        }
        Multistack.NonConfigurationInstance nonConfigurationInstance = (Multistack.NonConfigurationInstance) getLastCustomNonConfigurationInstance();

        multistack.onCreate(CHROMECAST.name(), savedInstanceState, nonConfigurationInstance, ChromeCastKey.create());
        multistack.onCreate(CLOUDSYNC.name(), savedInstanceState, nonConfigurationInstance, CloudSyncKey.create());
        multistack.onCreate(LIST.name(), savedInstanceState, nonConfigurationInstance, ListKey.create());
        multistack.onCreate(MAIL.name(), savedInstanceState, nonConfigurationInstance, MailKey.create());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        bottomNavigation.setOnMenuItemClickListener(new BottomNavigation.OnMenuItemSelectionListener() {
            @Override
            public void onMenuItemSelect(@IdRes int menuItemId, int itemIndex, boolean b) {
                Log.i("MainActivity", "Selected index: [" + menuItemId + "] at [" + itemIndex + "]");
                BackstackDelegate selectedStack = ServiceLocator.getService(MainActivity.this, StackType.values()[itemIndex].name());

            }

            @Override
            public void onMenuItemReselect(@IdRes int menuItemId, int itemIndex, boolean b) {

            }
        });
        chromeCastStack.setStateChanger(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return chromeCastStack.onRetainCustomNonConfigurationInstance();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        chromeCastStack.onPostResume();
    }

    @Override
    public void onBackPressed() {
        if(!chromeCastStack.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        chromeCastStack.onPause();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        multistack.persistToBackstack(root.getChildAt(0));
        multistack.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        chromeCastStack.onDestroy();
        super.onDestroy();
    }

    @Override
    public Object getSystemService(String name) {
        if(name.equals(CHROMECAST.name())) {
            return chromeCastStack;
        } else if(name.equals(CLOUDSYNC.name())) {
            return cloudSyncStack;
        } else if(name.equals(LIST.name())) {
            return listStack;
        } else if(name.equals(MAIL.name())) {
            return mailStack;
        }
        return super.getSystemService(name);
    }

    private void exchangeViewForKey(Key newKey) {
        chromeCastStack.persistViewToState(root.getChildAt(0));
        root.removeAllViews();
        Context newContext = new KeyContextWrapper(this, newKey);
        View view = LayoutInflater.from(newContext).inflate(newKey.layout(), root, false);
        chromeCastStack.restoreViewFromState(view);
        root.addView(view);
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            // no-op
            completionCallback.stateChangeComplete();
            return;
        }
        exchangeViewForKey(stateChange.topNewState());
        completionCallback.stateChangeComplete();
    }
}
