package com.zhuinden.simplestackexamplescoping.application;

import android.os.Bundle;

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

    @SuppressWarnings("DEPRECATION")
    private final OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if(!Navigator.onBackPressed(MainActivity.this)) {
                this.remove();
                onBackPressed();  // this is the only safe way to manually invoke onBackPressed when using onBackPressedDispatcher`
                MainActivity.this.getOnBackPressedDispatcher().addCallback(this);
            }
        }
    };

    @Override
    public final void onBackPressed() { // you cannot use `onBackPressed()` if you use `OnBackPressedDispatcher`
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        fragmentStateChanger = new DefaultFragmentStateChanger(getSupportFragmentManager(), R.id.container);

        getOnBackPressedDispatcher().addCallback(backPressedCallback); // this is required for `onBackPressedDispatcher` to work correctly

        Navigator.configure()
            .setStateChanger(new SimpleStateChanger(this)) //
            .setScopedServices(new DefaultServiceProvider()) //
            .install(this, findViewById(R.id.container), History.of(WordListKey.create()));
    }

    @Override
    public void onNavigationEvent(@Nonnull StateChange stateChange) {
        fragmentStateChanger.handleStateChange(stateChange);
    }
}