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

import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestack.navigator.StateKey;
import com.zhuinden.simplestack.navigator.ViewChangeHandler;
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler;

public final class Paths {
    public abstract static class Path implements Parcelable, StateKey {
        public abstract String getTitle();

        @NonNull
        @Override
        public ViewChangeHandler viewChangeHandler() {
            return new SegueViewChangeHandler();
        }
    }

    @AutoValue
    public abstract static class NoDetails
            extends Path {
        public static NoDetails create() {
            return new AutoValue_Paths_NoDetails();
        }

        @Override
        public String getTitle() {
            return "No Details";
        }

        @Override
        public int layout() {
            return R.layout.no_details;
        }
    }

    /**
     * Identifies screens in a master / detail relationship. Both master and detail screens
     * extend this class.
     * <p>
     * Not a lot of thought has been put into making a decent master / detail modeling here. Rather
     * this is an excuse to show off using Flow to build a responsive layout. See {@link
     * com.example.stackmasterdetail.view.TabletMasterDetailRoot}.
     */
    public abstract static class MasterDetailPath
            extends Path {
        /**
         * Returns the screen that shows the master list for this type of screen.
         * If this screen is the master, returns self.
         * <p>
         * For example, the {@link Conversation} and {@link Message} screens are both
         * "under" the master {@link ConversationList} screen. All three of these
         * screens return a {@link Conversation} from this method.
         */
        public abstract MasterDetailPath getMaster();

        public final boolean isMaster() {
            return equals(getMaster());
        }
    }

    public abstract static class ConversationPath
            extends MasterDetailPath {
        public abstract int conversationIndex();

        @Override
        public MasterDetailPath getMaster() {
            return new AutoValue_Paths_ConversationList();
        }
    }

    @AutoValue
    public abstract static class ConversationList
            extends ConversationPath {
        public ConversationList() {
        }

        @Override
        public int conversationIndex() {
            return -1;
        }

        public static ConversationList create() {
            return new AutoValue_Paths_ConversationList();
        }

        @Override
        public String getTitle() {
            return "Conversation List";
        }

        @Override
        public int layout() {
            return R.layout.conversation_list_view;
        }
    }

    @AutoValue
    public abstract static class Conversation
            extends ConversationPath {
        public static Conversation create(int conversationIndex) {
            return new AutoValue_Paths_Conversation(conversationIndex);
        }

        @Override
        public String getTitle() {
            return "Conversation";
        }

        @Override
        public int layout() {
            return R.layout.conversation_view;
        }
    }

    @AutoValue
    public abstract static class Message
            extends ConversationPath {
        public abstract int messageId();

        public static Message create(int messageIndex, int position) {
            return new AutoValue_Paths_Message(messageIndex, position);
        }

        @Override
        public String getTitle() {
            return "Message";
        }

        @Override
        public int layout() {
            return R.layout.message_view;
        }
    }

    public abstract static class FriendPath
            extends MasterDetailPath {
        public abstract int index();

        @Override
        public MasterDetailPath getMaster() {
            return new AutoValue_Paths_FriendList();
        }
    }

    @AutoValue
    public abstract static class FriendList
            extends FriendPath {
        @Override
        public int index() {
            return -1;
        }

        public static FriendList create() {
            return new AutoValue_Paths_FriendList();
        }

        @Override
        public String getTitle() {
            return "Friend List";
        }

        @Override
        public int layout() {
            return R.layout.friend_list_view;
        }
    }

    @AutoValue
    public abstract static class Friend
            extends FriendPath {
        public static Friend create(int position) {
            return new AutoValue_Paths_Friend(position);
        }

        @Override
        public String getTitle() {
            return "Friend";
        }

        @Override
        public int layout() {
            return R.layout.friend_view;
        }
    }

    private Paths() {
    }
}
