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
import android.util.Log;

import com.example.mortar.R;
import com.example.mortar.android.ActionBarOwner;
import com.example.mortar.core.SingletonComponent;
import com.example.mortar.model.Chat;
import com.example.mortar.model.Chats;
import com.example.mortar.model.Message;
import com.example.mortar.util.BaseKey;
import com.example.mortar.util.DaggerService;
import com.example.mortar.util.Subscope;
import com.example.mortar.util.ViewPresenter;
import com.example.mortar.view.ChatView;
import com.example.mortar.view.Confirmation;
import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.navigator.Navigator;
import com.zhuinden.statebundle.StateBundle;

import javax.inject.Inject;

import dagger.Provides;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableObserver;
import mortar.PopupPresenter;

public class ChatScreen
        extends BaseKey {
    private final int conversationIndex;

    public ChatScreen(int conversationIndex) {
        this.conversationIndex = conversationIndex;
    }

    @Override
    public void bindServices(ServiceTree.Node node) {
        SingletonComponent singletonComponent = DaggerService.get(node);
        node.bindService(DaggerService.SERVICE_NAME, //
                DaggerChatScreen_Component.builder() //
                        .singletonComponent(singletonComponent) //
                        .module(new Module(conversationIndex)) //
                        .build());
        node.bindService("PRESENTER", DaggerService.<Component>get(node).presenter()); // <-- for Bundleable callback
    }

    @Override
    public int layout() {
        return R.layout.chat_view;
    }

    @dagger.Component(dependencies = {SingletonComponent.class}, modules = {Module.class})
    @Subscope
    public interface Component extends SingletonComponent {
        Presenter presenter();

        void inject(ChatView chatView);
    }

    @dagger.Module
    public static class Module {
        private final int conversationIndex;

        public Module(int conversationIndex) {
            this.conversationIndex = conversationIndex;
        }

        @Provides
        Chat conversation(Chats chats) {
            return chats.getChat(conversationIndex);
        }
    }

    @Override
    public int hashCode() {
        return ChatScreen.class.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof ChatScreen && ((ChatScreen) obj).conversationIndex == conversationIndex;
    }

    @Override
    public String toString() {
        return "ChatScreen{" + "conversationIndex=" + conversationIndex + '}';
    }

    @Subscope
    public static class Presenter
            extends ViewPresenter<ChatView>
            implements ServiceTree.Scoped {
        private final Chat chat;
        private final ActionBarOwner actionBar;
        private final PopupPresenter<Confirmation, Boolean> confirmer;

        private Disposable running = Disposables.empty();

        @Inject
        public Presenter(Chat chat, ActionBarOwner actionBar) {
            this.chat = chat;
            this.actionBar = actionBar;
            this.confirmer = new PopupPresenter<Confirmation, Boolean>() {
                @Override
                protected void onPopupResult(Boolean confirmed) {
                    if(confirmed) {
                        Presenter.this.getView().toast("Haven't implemented that, friend.");
                    }
                }
            };
        }

        @Override
        public void dropView(ChatView view) {
            confirmer.dropView(view.getConfirmerPopup());
            super.dropView(view);
        }

        @Override
        public void onLoad(@Nullable StateBundle savedInstanceState) {
            if(!hasView()) {
                return;
            }

            ActionBarOwner.Config actionBarConfig = actionBar.getConfig();

            actionBarConfig = actionBarConfig.withAction(new ActionBarOwner.MenuAction("End", new Action() {
                @Override
                public void run() {
                    confirmer.show(new Confirmation("End Chat",
                            "Do you really want to leave this chat?",
                            "Yes",
                            "I guess not"));
                }
            }));

            actionBar.setConfig(actionBarConfig);

            confirmer.takeView(getView().getConfirmerPopup());

            running = chat.getMessages().subscribeWith(new DisposableObserver<Message>() {
                @Override
                public void onComplete() {
                    Log.w(getClass().getName(), "That's surprising, never thought this should end.");
                    running = Disposables.empty();
                }

                @Override
                public void onError(Throwable e) {
                    Log.w(getClass().getName(), "'sploded, will try again on next config change.");
                    Log.w(getClass().getName(), e);
                    running = Disposables.empty();
                }

                @Override
                public void onNext(Message message) {
                    if(!hasView()) {
                        return;
                    }
                    getView().getItems().add(message);
                }
            });
        }

        @Override
        public void onExitScope() {
            ensureStopped();
        }

        public void onConversationSelected(int position) {
            Navigator.getBackstack(getView().getContext()).goTo(new MessageScreen(chat.getId(), position));
        }

        public void visibilityChanged(boolean visible) {
            if(!visible) {
                ensureStopped();
            }
        }

        private void ensureStopped() {
            running.dispose();
        }
    }
}
