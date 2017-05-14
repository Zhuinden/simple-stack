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

import com.example.mortar.R;
import com.example.mortar.core.SingletonComponent;
import com.example.mortar.model.Chat;
import com.example.mortar.model.Chats;
import com.example.mortar.util.BaseKey;
import com.example.mortar.util.DaggerService;
import com.example.mortar.util.Subscope;
import com.example.mortar.util.ViewPresenter;
import com.example.mortar.view.ChatListView;
import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.navigator.Navigator;
import com.zhuinden.statebundle.StateBundle;

import java.util.List;

import javax.inject.Inject;

import dagger.Provides;

public class ChatListScreen
        extends BaseKey {
    @Override
    public void bindServices(ServiceTree.Node node) {
        SingletonComponent singletonComponent = DaggerService.get(node);
        node.bindService(DaggerService.SERVICE_NAME, //
                DaggerChatListScreen_Component.builder() //
                        .singletonComponent(singletonComponent) //
                        .build());
        node.bindService("PRESENTER", DaggerService.<Component>get(node).presenter()); // <-- for Bundleable callback
    }

    @Override
    public int layout() {
        return R.layout.chat_list_view;
    }

    @dagger.Component(dependencies = {SingletonComponent.class}, modules = {Module.class})
    @Subscope
    public interface Component extends SingletonComponent {
        Presenter presenter();

        void inject(ChatListView chatListView);
    }

    @dagger.Module
    public static class Module {
        @Provides
        @Subscope
        List<Chat> conversations(Chats chats) {
            return chats.getAll();
        }
    }

    @Subscope
    public static class Presenter
            extends ViewPresenter<ChatListView> {
        private final List<Chat> chats;

        @Inject
        Presenter(List<Chat> chats) {
            this.chats = chats;
        }

        @Override
        public void onLoad(StateBundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            if(!hasView()) {
                return;
            }
            getView().showConversations(chats);
        }

        public void onConversationSelected(int position) {
            Navigator.getBackstack(getView().getContext()).goTo(new ChatScreen(position));
        }
    }

    @Override
    public String toString() {
        return "ChatListScreen{}";
    }
}
