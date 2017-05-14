package com.example.mortar.core;

import com.example.mortar.android.ActionBarOwner;
import com.example.mortar.model.Chats;
import com.example.mortar.model.QuoteService;
import com.example.mortar.nodes.NodeStateManager;
import com.google.gson.Gson;
import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.KeyParceler;

import java.util.concurrent.Executor;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

/**
 * Created by Zhuinden on 2017.05.13..
 */

@Component(modules = {RootModule.class})
@Singleton
public interface SingletonComponent {
    KeyParceler keyParceler();

    ServiceTree serviceTree();

    NodeStateManager nodeStateManager();

    ActionBarOwner actionBarOwner();

    QuoteService quoteService();

    Retrofit retrofit();

    Chats chats();

    Executor messagePollThread();

    Gson gson();

    void inject(MortarDemoActivity mortarDemoActivity);
}
