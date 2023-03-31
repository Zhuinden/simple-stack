package com.zhuinden.simplestackexamplescoping.application;

import android.os.Bundle;

import com.zhuinden.simplestack.AheadOfTimeWillHandleBackChangedListener;
import com.zhuinden.simplestack.BackHandlingModel;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.SimpleStateChanger;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.navigator.Navigator;
import com.zhuinden.simplestackexamplescoping.R;
import com.zhuinden.simplestackexamplescoping.features.words.WordListKey;
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger;
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider;

import javax.annotation.Nonnull;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity
    extends AppCompatActivity
    implements SimpleStateChanger.NavigationHandler {
    private DefaultFragmentStateChanger fragmentStateChanger;

    private Backstack backstack;

    private OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {
            backstack.goBack();
        }
    };

    private AheadOfTimeWillHandleBackChangedListener updateBackPressedCallback = new AheadOfTimeWillHandleBackChangedListener() {
        @Override
        public void willHandleBackChanged(boolean willHandleBack) {
            backPressedCallback.setEnabled(willHandleBack);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        fragmentStateChanger = new DefaultFragmentStateChanger(getSupportFragmentManager(), R.id.container);

        getOnBackPressedDispatcher().addCallback(backPressedCallback);

        backstack = Navigator.configure()
            .setBackHandlingModel(BackHandlingModel.AHEAD_OF_TIME)
            .setStateChanger(new SimpleStateChanger(this)) //
            .setScopedServices(new DefaultServiceProvider()) //
            .install(this, findViewById(R.id.container), History.of(WordListKey.create()));

        backPressedCallback.setEnabled(backstack.willHandleAheadOfTimeBack());
        backstack.addAheadOfTimeWillHandleBackChangedListener(updateBackPressedCallback);
    }

    @Override
    protected void onDestroy() {
        backstack.removeAheadOfTimeWillHandleBackChangedListener(updateBackPressedCallback);
        super.onDestroy();
    }

    @Override
    public void onNavigationEvent(@Nonnull StateChange stateChange) {
        fragmentStateChanger.handleStateChange(stateChange);
    }
}