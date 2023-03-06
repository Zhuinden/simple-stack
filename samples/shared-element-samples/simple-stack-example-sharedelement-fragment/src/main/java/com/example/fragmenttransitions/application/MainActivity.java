package com.example.fragmenttransitions.application;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fragmenttransitions.features.kitten.grid.KittenGridKey;
import com.example.fragmenttransitions.R;
import com.example.fragmenttransitions.core.navigation.SharedElementFragmentStateChanger;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.SimpleStateChanger;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.navigator.Navigator;

import javax.annotation.Nonnull;

public class MainActivity
    extends AppCompatActivity
    implements SimpleStateChanger.NavigationHandler {
    @SuppressWarnings("DEPRECATION")
    private final OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if(!Navigator.onBackPressed(MainActivity.this)) {
                this.remove();
                onBackPressed(); // this is the reliable way to handle back for now
                MainActivity.this.getOnBackPressedDispatcher().addCallback(this);
            }
        }
    };

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

        Navigator.configure()
            .setStateChanger(new SimpleStateChanger(this))
            .install(this, findViewById(R.id.container), History.of(KittenGridKey.create()));
    }

    @Override
    public void onNavigationEvent(@Nonnull StateChange stateChange) {
        fragmentStateChanger.handleStateChange(stateChange);
    }
}
