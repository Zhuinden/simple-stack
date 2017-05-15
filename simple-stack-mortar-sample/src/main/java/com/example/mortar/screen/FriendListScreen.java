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
package com.example.mortar.screen;

import android.support.annotation.Nullable;

import com.example.mortar.R;
import com.example.mortar.core.SingletonComponent;
import com.example.mortar.model.Chats;
import com.example.mortar.model.User;
import com.example.mortar.util.BaseKey;
import com.example.mortar.util.DaggerService;
import com.example.mortar.util.Subscope;
import com.example.mortar.util.ViewPresenter;
import com.example.mortar.view.FriendListView;
import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.navigator.Navigator;
import com.zhuinden.statebundle.StateBundle;

import java.util.List;

import javax.inject.Inject;

import dagger.Provides;

public class FriendListScreen
        extends BaseKey {

    @Override
    public void bindServices(ServiceTree.Node node) {
        SingletonComponent singletonComponent = DaggerService.get(node);
        node.bindService(DaggerService.SERVICE_NAME, //
                DaggerFriendListScreen_Component.builder() //
                        .singletonComponent(singletonComponent) //
                        .build());
        node.bindService("PRESENTER", DaggerService.<Component>get(node).presenter()); // <-- for Bundleable callback
    }

    @Override
    public int layout() {
        return R.layout.friend_list_view;
    }

    @Override
    public int hashCode() {
        return FriendListScreen.class.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof FriendListScreen;
    }

    @Override
    public String toString() {
        return "FriendListScreen{}";
    }

    @dagger.Component(dependencies = {SingletonComponent.class}, modules = {Module.class})
    @Subscope
    public interface Component {
        Presenter presenter();

        void inject(FriendListView friendListView);
    }

    @dagger.Module
    public static class Module {
        @Provides
        @Subscope
        List<User> provideFriends(Chats chats) {
            return chats.getFriends();
        }
    }

    @Subscope
    public static class Presenter
            extends ViewPresenter<FriendListView> {
        private final List<User> friends;

        @Inject
        Presenter(List<User> friends) {
            this.friends = friends;
        }

        @Override
        public void onLoad(@Nullable StateBundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            if(!hasView()) {
                return;
            }
            getView().showFriends(friends);
        }

        public void onFriendSelected(int position) {
            Navigator.getBackstack(getView().getContext()).goTo(new FriendScreen(position));
        }
    }
}
