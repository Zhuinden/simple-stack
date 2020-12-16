package com.example.fragmenttransitions;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.SimpleStateChanger;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestack.navigator.Navigator;

import javax.annotation.Nonnull;

/**
 * Main activity that holds our fragments
 *
 * @author bherbst
 */
public class MainActivity
        extends AppCompatActivity
        implements SimpleStateChanger.NavigationHandler {
    static final String TAG = "MainActivity";

    FragmentStateChanger fragmentStateChanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentStateChanger = new FragmentStateChanger(getSupportFragmentManager(), R.id.container);

        Navigator.configure()
                .setStateChanger(new SimpleStateChanger(this))
                .install(this, findViewById(R.id.container), History.of(GridKey.create()));
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
