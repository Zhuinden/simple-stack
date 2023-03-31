package com.example.fragmenttransitions.application;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fragmenttransitions.features.kitten.grid.KittenGridKey;
import com.example.fragmenttransitions.R;
import com.example.fragmenttransitions.core.navigation.SharedElementFragmentStateChanger;
import com.zhuinden.simplestack.AheadOfTimeWillHandleBackChangedListener;
import com.zhuinden.simplestack.BackHandlingModel;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.SimpleStateChanger;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.navigator.Navigator;

import javax.annotation.Nonnull;

public class MainActivity
    extends AppCompatActivity
    implements SimpleStateChanger.NavigationHandler {
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
    ;

    @Override
    public final void onBackPressed() { // you cannot use `onBackPressed()` if you use `OnBackPressedDispatcher`
        super.onBackPressed();
    }

    static final String TAG = "MainActivity";

    SharedElementFragmentStateChanger fragmentStateChanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentStateChanger = new SharedElementFragmentStateChanger(getSupportFragmentManager(), R.id.container);

        getOnBackPressedDispatcher().addCallback(backPressedCallback); // this is the reliable way to handle back for now

        backstack = Navigator.configure()
            .setBackHandlingModel(BackHandlingModel.AHEAD_OF_TIME)
            .setStateChanger(new SimpleStateChanger(this))
            .install(this, findViewById(R.id.container), History.of(KittenGridKey.create()));


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
