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

package com.example.stackmasterdetailfrag.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.stackmasterdetailfrag.R;
import com.example.stackmasterdetailfrag.paths.conversation.conversationlist.ConversationListPath;
import com.example.stackmasterdetailfrag.paths.friend.friendlist.FriendListPath;
import com.example.stackmasterdetailfrag.util.BackstackService;
import com.example.stackmasterdetailfrag.util.FragmentManagerService;
import com.example.stackmasterdetailfrag.util.MasterDetailStateClearStrategy;
import com.example.stackmasterdetailfrag.util.pathview.HandlesBack;
import com.example.stackmasterdetailfrag.util.pathview.TabletMasterDetailRoot;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;

public class MainActivity
        extends AppCompatActivity
        implements StateChanger {
    public static final String TAG = "MainActivity";

    private StateChanger container;
    private HandlesBack containerAsBackTarget;

    private BackstackDelegate backstackDelegate;

    /**
     * Pay attention to the {@link #setContentView} call here. It's creating a responsive layout
     * for us.
     * <p>
     * Notice that the app has two root_layout files. The main one, in {@code res/layout} is used by
     * mobile devices and by tablets in portrait orientation. It holds a generic {@link
     * com.example.stackmasterdetailfrag.util.pathview.SinglePaneRoot}.
     * <p>
     * The interesting one, loaded by tablets in landscape mode, is {@code res/layout-sw600dp-land}.
     * It loads a {@link TabletMasterDetailRoot}, with a master list on the
     * left and a detail view on the right.
     * <p>
     * But this master activity knows nothing about those two view types. It only requires that
     * the view loaded by {@code root_layout.xml} implements the StateChanger interface,
     * to render whatever is appropriate for the screens received from {@link com.zhuinden.simplestack.Backstack} via
     * {@link #handleStateChange}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        backstackDelegate = new BackstackDelegate(null);
        backstackDelegate.setStateClearStrategy(new MasterDetailStateClearStrategy());
        backstackDelegate.onCreate(savedInstanceState,
                getLastCustomNonConfigurationInstance(), HistoryBuilder.single(ConversationListPath.create()));
        backstackDelegate.registerForLifecycleCallbacks(this);
        setContentView(R.layout.root_layout);
        container = (StateChanger) findViewById(R.id.container);
        containerAsBackTarget = (HandlesBack) container;
        backstackDelegate.setStateChanger(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return backstackDelegate.onRetainCustomNonConfigurationInstance();
    }

    @Override
    public Object getSystemService(String name) {
        if(name.equals(FragmentManagerService.FRAGMENT_MANAGER_TAG)) {
            return getSupportFragmentManager();
        }
        if(name.equals(BackstackService.BACKSTACK_TAG)) {
            return backstackDelegate.getBackstack();
        }
        if(name.equals(BackstackService.DELEGATE_TAG)) {
            return backstackDelegate;
        }
        if(name.equals(TAG)) {
            return this;
        }
        return super.getSystemService(name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Friends").setShowAsActionFlags(SHOW_AS_ACTION_ALWAYS).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                BackstackService.get(MainActivity.this).setHistory(HistoryBuilder.newBuilder() //
                        .add(ConversationListPath.create()) //
                        .add(FriendListPath.create()) //
                        .build(), StateChange.FORWARD);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(containerAsBackTarget.onBackPressed()) {
            return;
        }
        if(backstackDelegate.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void handleStateChange(@NonNull StateChange traversal, @NonNull final StateChanger.Callback callback) {
        Path path = traversal.topNewState();
        setTitle(path.getTitle());
        ActionBar actionBar = getSupportActionBar();
        boolean canGoBack = traversal.getNewState().size() > 1;
        actionBar.setDisplayHomeAsUpEnabled(canGoBack);
        actionBar.setHomeButtonEnabled(canGoBack);
        container.handleStateChange(traversal, new StateChanger.Callback() {
            @Override
            public void stateChangeComplete() {
                invalidateOptionsMenu();
                callback.stateChangeComplete();
            }
        });
    }
}
