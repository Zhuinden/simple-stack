/*
 * Copyright 2013 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.mortar.core;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.mortar.R;
import com.example.mortar.android.ActionBarOwner;
import com.example.mortar.nodes.NodeClearManager;
import com.example.mortar.nodes.NodeCreationManager;
import com.example.mortar.nodes.NodeStateManager;
import com.example.mortar.nodes.TreeNodes;
import com.example.mortar.screen.ChatListScreen;
import com.example.mortar.screen.FriendListScreen;
import com.example.mortar.util.BackSupport;
import com.example.mortar.util.DaggerService;
import com.example.mortar.util.NodePrinter;
import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.KeyParceler;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestack.navigator.DefaultStateChanger;
import com.zhuinden.simplestack.navigator.Navigator;
import com.zhuinden.simplestack.navigator.StateKey;
import com.zhuinden.statebundle.StateBundle;

import javax.inject.Inject;

import io.reactivex.functions.Action;

import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;

/**
 * A well intentioned but overly complex sample that demonstrates
 * the use of Mortar, Flow and Dagger in a single app.
 */
public class MortarDemoActivity
        extends AppCompatActivity
        implements ActionBarOwner.Activity, StateChanger {

    private ServiceTree.Node activityScope;
    private ActionBarOwner.MenuAction actionBarMenuAction;

    @Inject
    ActionBarOwner actionBarOwner;

    @Inject
    KeyParceler keyParceler;

    @Inject
    ServiceTree serviceTree;

    @Inject
    NodeStateManager nodeStateManager;

    private FrameLayout container;

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        StateKey newScreen = stateChange.topNewState();
        String title = newScreen.getClass().getSimpleName();
        ActionBarOwner.MenuAction menu = new ActionBarOwner.MenuAction("Friends", new Action() {
            @Override
            public void run()
                    throws Exception {
                Navigator.getBackstack(MortarDemoActivity.this).goTo(new FriendListScreen());
            }
        });
        actionBarOwner.setConfig(new ActionBarOwner.Config(false, !(newScreen instanceof ChatListScreen), title, menu));
        completionCallback.stateChangeComplete();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            StateBundle rootBundle = savedInstanceState.getParcelable(NodeStateManager.SERVICE_STATES);
            if(rootBundle != null) { // global service state is restored after process death
                SingletonComponent singletonComponent = DaggerService.get(this); // not yet injected by field injection
                ServiceTree _serviceTree = singletonComponent.serviceTree();
                _serviceTree.registerRootService(NodeStateManager.SERVICE_STATES, rootBundle);
            }
        }
        ServiceTree.Node parentScope = TreeNodes.getNode(getApplication());

        String scopeName = getLocalClassName() + "-task-" + getTaskId();

        if(!parentScope.hasChild(scopeName)) {
            parentScope.createChild(scopeName);
        }
        activityScope = parentScope.getChild(scopeName);
        DaggerService.<SingletonComponent>get(this).inject(this);

        actionBarOwner.takeView(this);

        setContentView(R.layout.root_layout);
        container = (FrameLayout) findViewById(R.id.container);
        Navigator.configure()
                .setKeyParceler(keyParceler)
                .addStateChangeCompletionListener(new NodeClearManager(serviceTree,
                        nodeStateManager)) // to delete un-used mortar scopes
                .setStateChanger(DefaultStateChanger.configure()
                        .setExternalStateChanger(this)
                        .setContextCreationStrategy(new NodeCreationManager(serviceTree,
                                activityScope,
                                nodeStateManager))
                        .create(this, container))
                .install(this, container, HistoryBuilder.single(new ChatListScreen()));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(NodeStateManager.SERVICE_STATES, nodeStateManager.persistStates());
    }

    @Override
    public Object getSystemService(String name) {
        if(TreeNodes.NODE_TAG.equals(name)) {
            return activityScope;
        }
        if(activityScope != null && activityScope.hasService(name)) {
            return activityScope.getService(name);
        }
        return super.getSystemService(name);
    }

    /**
     * Inform the view about back events.
     */
    @Override
    public void onBackPressed() {
        if(!BackSupport.onBackPressed(container.getChildAt(0))) {
            super.onBackPressed();
        }
    }

    /**
     * Inform the view about up events.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            boolean handled = BackSupport.onBackPressed(container.getChildAt(0));
            if(handled) {
                return true;
            }
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Configure the action bar menu as required by {@link ActionBarOwner.Activity}.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(actionBarMenuAction != null) {
            menu.add(actionBarMenuAction.title)
                    .setShowAsActionFlags(SHOW_AS_ACTION_ALWAYS)
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            try {
                                actionBarMenuAction.action.run();
                            } catch(Exception e) {
                                throw new RuntimeException(e);
                            }
                            return true;
                        }
                    });
        }
        menu.add("Log Scope Hierarchy").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.i("MORTAR", NodePrinter.scopeHierarchyToString(activityScope));
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onDestroy() {
        actionBarOwner.dropView(this);
        actionBarOwner.setConfig(null);

        // activityScope may be null in case isWrongInstance() returned true in onCreate()
        if(isFinishing() && activityScope != null) {
            activityScope.removeNodeAndChildren();
            activityScope = null;
        }

        super.onDestroy();
    }

    @Override
    public void setShowHomeEnabled(boolean enabled) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
    }

    @Override
    public void setUpButtonEnabled(boolean enabled) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(enabled);
        actionBar.setHomeButtonEnabled(enabled);
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void setMenu(ActionBarOwner.MenuAction action) {
        if(action != actionBarMenuAction) {
            actionBarMenuAction = action;
            invalidateOptionsMenu();
        }
    }
}
