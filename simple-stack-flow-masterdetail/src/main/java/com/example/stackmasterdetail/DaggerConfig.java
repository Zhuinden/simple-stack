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

package com.example.stackmasterdetail;

import com.example.stackmasterdetail.model.Conversation;
import com.example.stackmasterdetail.model.User;
import com.example.stackmasterdetail.view.ConversationListView;
import com.example.stackmasterdetail.view.ConversationView;
import com.example.stackmasterdetail.view.FriendListView;
import com.example.stackmasterdetail.view.FriendView;
import com.example.stackmasterdetail.view.MessageView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import flow.StateParceler;
import java.util.List;
import javax.inject.Singleton;

@Module(
    injects = {
        ConversationView.class, //
        ConversationListView.class, //
        FriendView.class, //
        FriendListView.class, //
        MessageView.class,
    },
    library = true)
public class DaggerConfig {
  @Provides List<Conversation> provideConversations() {
    return SampleData.CONVERSATIONS;
  }

  @Provides List<User> provideFriends() {
    return SampleData.FRIENDS;
  }

  @Provides @Singleton Gson provideGson() {
    return new GsonBuilder().create();
  }

  @Provides @Singleton StateParceler provideParcer(Gson gson) {
    return new GsonParceler(gson);
  }
}
