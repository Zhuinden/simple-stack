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

package com.example.stackmasterdetail;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.example.stackmasterdetail.pathview.HandlesBack;
import com.example.stackmasterdetail.util.BackstackService;
import com.example.stackmasterdetail.util.MasterDetailStateClearStrategy;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestack.navigator.Navigator;

import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;

public class MainActivity
        extends AppCompatActivity
        implements StateChanger {
    public static final String TAG = "MainActivity";

    private ViewGroup container;

    private StateChanger containerAsStateChanger;
    private HandlesBack containerAsBackTarget;

    /**
     * Pay attention to the {@link #setContentView} call here. It's creating a responsive layout
     * for us.
     * <p>
     * Notice that the app has two root_layout files. The main one, in {@code res/layout} is used by
     * mobile devices and by tablets in portrait orientation. It holds a generic {@link
     * com.example.stackmasterdetail.pathview.FramePathContainerView}.
     * <p>
     * The interesting one, loaded by tablets in landscape mode, is {@code res/layout-sw600dp-land}.
     * It loads a {@link com.example.stackmasterdetail.view.TabletMasterDetailRoot}, with a master list on the
     * left and a detail view on the right.
     * <p>
     * But this master activity knows nothing about those two view types. It only requires that
     * the view loaded by {@code root_layout.xml} implements the {@link com.example.stackmasterdetail.util.Container} interface,
     * to render whatever is appropriate for the screens received from {@link com.zhuinden.simplestack.Backstack} via
     * {@link #handleStateChange}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        setContentView(R.layout.root_layout);
        container = (ViewGroup) findViewById(R.id.container);
        containerAsStateChanger = (StateChanger) container;
        containerAsBackTarget = (HandlesBack) containerAsStateChanger;
        Navigator.configure()
                .setStateChanger(this)
                .setStateClearStrategy(new MasterDetailStateClearStrategy())
                .setShouldPersistContainerChild(false)
                .install(this, container, HistoryBuilder.single(Paths.ConversationList.create()));
    }

    @Override
    public Object getSystemService(String name) {
        if(name.equals(BackstackService.BACKSTACK_TAG)) {
            return Navigator.getBackstack(this);
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
                        .add(Paths.ConversationList.create()) //
                        .add(Paths.FriendList.create()) //
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
        if(Navigator.onBackPressed(this)) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void handleStateChange(StateChange traversal, final StateChanger.Callback callback) {
        Paths.Path path = traversal.topNewState();
        setTitle(path.getTitle());
        ActionBar actionBar = getSupportActionBar();
        boolean canGoBack = traversal.getNewState().size() > 1;
        actionBar.setDisplayHomeAsUpEnabled(canGoBack);
        actionBar.setHomeButtonEnabled(canGoBack);
        containerAsStateChanger.handleStateChange(traversal, new StateChanger.Callback() {
            @Override
            public void stateChangeComplete() {
                invalidateOptionsMenu();
                callback.stateChangeComplete();
            }
        });
    }
}
