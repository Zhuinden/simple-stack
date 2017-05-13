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

import android.os.Bundle;

import com.example.mortar.R;
import com.example.mortar.core.SingletonComponent;
import com.example.mortar.model.Chats;
import com.example.mortar.model.Message;
import com.example.mortar.util.DaggerService;
import com.example.mortar.util.BaseKey;
import com.example.mortar.util.Subscope;
import com.example.mortar.util.ViewPresenter;
import com.example.mortar.view.MessageView;
import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.navigator.Navigator;

import javax.inject.Inject;

import dagger.Provides;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class MessageScreen
        extends BaseKey {
    private final int chatId;
    private final int messageId;

    public MessageScreen(int chatId, int messageId) {
        this.chatId = chatId;
        this.messageId = messageId;
    }

    @Override
    public void bindServices(ServiceTree.Node node) {
        SingletonComponent singletonComponent = DaggerService.get(node);
        node.bindService(DaggerService.SERVICE_NAME, //
                DaggerMessageScreen_Component.builder() //
                        .singletonComponent(singletonComponent) //
                        .module(new Module(chatId, messageId)) //
                        .build());
    }

    @Override
    public int layout() {
        return R.layout.message_view;
    }

    @dagger.Component(dependencies = {SingletonComponent.class}, modules = {Module.class})
    @Subscope
    public interface Component {
        void inject(MessageView messageView);
    }

    @dagger.Module
    public static class Module {
        private final int chatId;
        private final int messageId;

        public Module(int chatId, int messageId) {
            this.chatId = chatId;
            this.messageId = messageId;
        }

        @Provides
        Observable<Message> provideMessage(Chats chats) {
            return chats.getChat(chatId).getMessage(messageId);
        }
    }

    @Subscope
    public static class Presenter
            extends ViewPresenter<MessageView> {
        private final Observable<Message> messageSource;

        private Message message;

        @Inject
        Presenter(Observable<Message> messageSource) {
            this.messageSource = messageSource;
        }

        @Override
        public void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            if(!hasView()) {
                return;
            }

            messageSource.subscribe(new Consumer<Message>() {
                @Override
                public void accept(@NonNull Message message)
                        throws Exception {
                    if(!hasView()) {
                        return;
                    }
                    Presenter.this.message = message;
                    MessageView view = getView();
                    view.setUser(message.from.name);
                    view.setMessage(message.body);
                }
            });
        }

        public void onUserSelected() {
            if(message == null) {
                return;
            }
            int position = message.from.id;
            if(position != -1) {
                Navigator.getBackstack(getView().getContext()).goTo(new FriendScreen(position));
            }
        }
    }
}


