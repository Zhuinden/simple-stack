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
package com.example.mortar.core;

import com.example.mortar.android.ActionBarOwner;
import com.example.mortar.model.Chats;
import com.example.mortar.model.QuoteService;
import com.example.mortar.nodes.NodeStateManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.DefaultKeyParceler;
import com.zhuinden.simplestack.KeyParceler;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Defines app-wide singletons.
 */
@Module(includes = {ActionBarOwner.ActionBarModule.class, Chats.Module.class})
public class RootModule {
    private final ServiceTree serviceTree;

    public RootModule(ServiceTree serviceTree) {
        this.serviceTree = serviceTree;
    }

    @Provides
    @Singleton
    Gson gson() {
        return new GsonBuilder().create();
    }

    @Provides
    @Singleton
    KeyParceler keyParceler(Gson gson) {
        //return new GsonParceler(gson); // removed to support seamless auto-parcel integration
        return new DefaultKeyParceler();
    }

    @Provides
    @Singleton
    Retrofit retrofit(Gson gson) {
        return new Retrofit.Builder().baseUrl("http://quotes.rest/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    @Provides
    @Singleton
    QuoteService quoteService(Retrofit retrofit) {
        return retrofit.create(QuoteService.class);
    }

    @Provides
    ServiceTree serviceTree() {
        return serviceTree;
    }

    @Provides
    @Singleton
    NodeStateManager nodeStateManager(ServiceTree serviceTree) {
        return new NodeStateManager(serviceTree);
    }
}
