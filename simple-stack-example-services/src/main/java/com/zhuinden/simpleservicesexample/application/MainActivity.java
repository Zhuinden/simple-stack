package com.zhuinden.simpleservicesexample.application;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simpleservicesexample.R;
import com.zhuinden.simpleservicesexample.presentation.paths.a.A;
import com.zhuinden.simpleservicesexample.utils.ServiceLocator;
import com.zhuinden.simpleservicesexample.utils.ServiceManager;
import com.zhuinden.simpleservicesexample.utils.StackService;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestack.navigator.DefaultStateChanger;
import com.zhuinden.simplestack.navigator.Navigator;
import com.zhuinden.statebundle.StateBundle;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity
        extends AppCompatActivity
        implements StateChanger {
    private static final String TAG = "MainActivity";

    @BindView(R.id.root)
    RelativeLayout root;

    ServiceTree serviceTree;

    ServiceManager serviceManager;

    public static class NonConfigurationInstance {
        ServiceManager serviceManager;

        private NonConfigurationInstance(ServiceManager serviceManager) {
            this.serviceManager = serviceManager;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        NonConfigurationInstance nonConfigurationInstance = (NonConfigurationInstance) getLastCustomNonConfigurationInstance();
        if(nonConfigurationInstance != null) {
            serviceManager = nonConfigurationInstance.serviceManager;
            serviceTree = serviceManager.getServiceTree();
        } else {
            serviceTree = new ServiceTree();
            serviceTree.createRootNode(TAG);
            serviceManager = new ServiceManager(serviceTree, TAG);
        }

        serviceManager.setRestoredStates(savedInstanceState != null ? savedInstanceState.getParcelable(ServiceManager.SERVICE_STATES) : new StateBundle());

        Navigator.configure()
                .setStateChanger(DefaultStateChanger.configure().setExternalStateChanger(this).create(this, root))
                .install(this, root, HistoryBuilder.single(A.create()));
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return new NonConfigurationInstance(serviceManager);
    }

    @Override
    public void onBackPressed() {
        if(!Navigator.onBackPressed(this)) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ServiceManager.SERVICE_STATES, serviceManager.persistStates());
    }

    @Override
    public Object getSystemService(String name) {
        if(StackService.TAG.equals(name)) {
            return Navigator.getBackstack(this);
        }
        if(ServiceLocator.SERVICE_TREE.equals(name)) {
            return serviceTree;
        }
        return super.getSystemService(name);
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        serviceManager.setupServices(stateChange);
        completionCallback.stateChangeComplete();
    }
}
