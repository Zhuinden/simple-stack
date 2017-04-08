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

package com.example.stackmasterdetail.application.injection;

import com.example.stackmasterdetail.data.SampleData;
import com.example.stackmasterdetail.data.model.Conversation;
import com.example.stackmasterdetail.data.model.User;
import com.example.stackmasterdetail.paths.conversation.conversation.ConversationView;
import com.example.stackmasterdetail.paths.conversation.conversationlist.ConversationListView;
import com.example.stackmasterdetail.paths.conversation.message.MessageView;
import com.example.stackmasterdetail.paths.friend.friend.FriendView;
import com.example.stackmasterdetail.paths.friend.friendlist.FriendListView;

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
