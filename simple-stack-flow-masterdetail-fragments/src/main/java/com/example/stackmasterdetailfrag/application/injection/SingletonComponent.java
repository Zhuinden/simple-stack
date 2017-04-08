/*
 * Copyright 2014 Square Inc.
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

package com.example.stackmasterdetailfrag.application.injection;

import com.example.stackmasterdetailfrag.data.SampleData;
import com.example.stackmasterdetailfrag.data.model.Conversation;
import com.example.stackmasterdetailfrag.data.model.User;
import com.example.stackmasterdetailfrag.paths.conversation.conversation.ConversationView;
import com.example.stackmasterdetailfrag.paths.conversation.conversationlist.ConversationListView;
import com.example.stackmasterdetailfrag.paths.conversation.message.MessageView;
import com.example.stackmasterdetailfrag.paths.friend.friend.FriendView;
import com.example.stackmasterdetailfrag.paths.friend.friendlist.FriendListView;

import java.util.List;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Component(modules = {SingletonComponent.ComponentModule.class})
@Singleton
public interface SingletonComponent {
  void inject(ConversationView conversationView);
  void inject(ConversationListView conversationListView);
  void inject(FriendView friendView);
  void inject(FriendListView friendListView);
  void inject(MessageView messageView);

  @Module
  public static class ComponentModule {
    @Provides List<Conversation> provideConversations() {
      return SampleData.CONVERSATIONS;
    }

    @Provides List<User> provideFriends() {
      return SampleData.FRIENDS;
    }
  }
}
