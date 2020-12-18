package com.example.fragmenttransitions.application;

import android.os.Bundle;

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
    static final String TAG = "MainActivity";

    SharedElementFragmentStateChanger fragmentStateChanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentStateChanger = new SharedElementFragmentStateChanger(getSupportFragmentManager(), R.id.container);

        Navigator.configure()
                .setStateChanger(new SimpleStateChanger(this))
                .install(this, findViewById(R.id.container), History.of(KittenGridKey.create()));
    }

    @Override
    public void onBackPressed() {
        if(!Navigator.onBackPressed(this)) {
            super.onBackPressed();
        }
    }

    @Override
    public void onNavigationEvent(@Nonnull StateChange stateChange) {
        fragmentStateChanger.handleStateChange(stateChange);
    }
}
