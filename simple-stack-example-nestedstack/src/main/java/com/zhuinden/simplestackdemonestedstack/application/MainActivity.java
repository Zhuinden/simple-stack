package com.zhuinden.simplestackdemonestedstack.application;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestack.navigator.DefaultStateChanger;
import com.zhuinden.simplestack.navigator.Navigator;
import com.zhuinden.simplestackdemonestedstack.R;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.main.MainKey;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.other.OtherKey;
import com.zhuinden.simplestackdemonestedstack.util.NestSupportServiceManager;
import com.zhuinden.simplestackdemonestedstack.util.PreserveTreeScopesStrategy;
import com.zhuinden.simplestackdemonestedstack.util.ServiceLocator;
import com.zhuinden.statebundle.StateBundle;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity
        extends AppCompatActivity
        implements StateChanger {
    private static final String TAG = "MainActivity";

    public static MainActivity get(Context context) {
        //noinspection ResourceType
        return (MainActivity) context.getSystemService(TAG);
    }

    @BindView(R.id.view_root)
    FrameLayout root;

    Backstack backstack;

    private boolean isAnimating;

    public void setAnimating(boolean animating) {
        this.isAnimating = animating;
    }

    static class NonConfigurationInstance {
        NestSupportServiceManager serviceManager;

        private NonConfigurationInstance(NestSupportServiceManager serviceManager) {
            this.serviceManager = serviceManager;
        }
    }

    NestSupportServiceManager serviceManager;
    ServiceTree serviceTree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        NonConfigurationInstance nonConfigurationInstance = (NonConfigurationInstance) getLastCustomNonConfigurationInstance();
        if(nonConfigurationInstance != null) {
            serviceManager = nonConfigurationInstance.serviceManager;
            serviceTree = serviceManager.getServiceTree();
        } else {
            serviceTree = new ServiceTree();
            serviceTree.createRootNode(TAG);
            serviceManager = new NestSupportServiceManager(serviceTree);
            serviceManager.setRestoredStates(savedInstanceState != null ? savedInstanceState.getParcelable("SERVICE_BUNDLE") : new StateBundle());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        backstack = Navigator.configure()
                .setStateChanger(DefaultStateChanger.configure().setExternalStateChanger(this).create(this, root))
                .setStateClearStrategy(new PreserveTreeScopesStrategy(serviceTree))
                .install(this, root, HistoryBuilder.single(MainKey.create()));
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return new NonConfigurationInstance(serviceManager);
    }

    @Override
    public void onBackPressed() {
        if(!serviceManager.handleBack(this)) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("SERVICE_BUNDLE", serviceManager.persistStates());
    }

    @Override
    public Object getSystemService(String name) {
        if(MainActivity.TAG.equals(name)) {
            return this;
        }
        if(ServiceLocator.SERVICE_TREE.equals(name)) {
            return serviceTree;
        }
        if(NestSupportServiceManager.SERVICE_MANAGER.equals(name)) {
            return serviceManager;
        }
        return super.getSystemService(name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_main_next) {
            backstack.goTo(OtherKey.create());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        serviceManager.setupServices(stateChange);
        completionCallback.stateChangeComplete();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return !isAnimating && super.dispatchTouchEvent(ev);
    }
}