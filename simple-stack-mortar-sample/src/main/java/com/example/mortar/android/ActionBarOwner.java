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
package com.example.mortar.android;

import android.content.Context;

import com.example.mortar.util.ViewPresenter;
import com.zhuinden.statebundle.StateBundle;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.functions.Action;


/**
 * Allows shared configuration of the Android ActionBar.
 */
public class ActionBarOwner
        extends ViewPresenter<ActionBarOwner.Activity> {
    public interface Activity {
        void setShowHomeEnabled(boolean enabled);

        void setUpButtonEnabled(boolean enabled);

        void setTitle(CharSequence title);

        void setMenu(MenuAction action);

        Context getContext();
    }

    public static class Config {
        public final boolean showHomeEnabled;
        public final boolean upButtonEnabled;
        public final CharSequence title;
        public final MenuAction action;

        public Config(boolean showHomeEnabled, boolean upButtonEnabled, CharSequence title, MenuAction action) {
            this.showHomeEnabled = showHomeEnabled;
            this.upButtonEnabled = upButtonEnabled;
            this.title = title;
            this.action = action;
        }

        public Config withAction(MenuAction action) {
            return new Config(showHomeEnabled, upButtonEnabled, title, action);
        }
    }

    public static class MenuAction {
        public final CharSequence title;
        public final Action action;

        public MenuAction(CharSequence title, Action action) {
            this.title = title;
            this.action = action;
        }
    }

    private Config config;

    ActionBarOwner() {
    }

    @Override
    public void onLoad(StateBundle savedInstanceState) {
        if(config != null) {
            update();
        }
    }

    public void setConfig(Config config) {
        this.config = config;
        update();
    }

    public Config getConfig() {
        return config;
    }

    private void update() {
        if(!hasView()) {
            return;
        }
        Activity activity = getView();

        activity.setShowHomeEnabled(config.showHomeEnabled);
        activity.setUpButtonEnabled(config.upButtonEnabled);
        activity.setTitle(config.title);
        activity.setMenu(config.action);
    }

    @Module
    public static class ActionBarModule {
        @Provides
        @Singleton
        ActionBarOwner provideActionBarOwner() {
            return new ActionBarOwner();
        }
    }
}
