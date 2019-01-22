package com.example.fragmenttransitions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

/**
 * Main activity that holds our fragments
 *
 * @author bherbst
 */
public class MainActivity
        extends AppCompatActivity
        implements StateChanger {
    static final String TAG = "MainActivity";

    BackstackDelegate backstackDelegate;
    FragmentStateChanger fragmentStateChanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        backstackDelegate = new BackstackDelegate();
        backstackDelegate.onCreate(savedInstanceState, getLastCustomNonConfigurationInstance(), History.single(GridKey.create()));
        backstackDelegate.registerForLifecycleCallbacks(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentStateChanger = new FragmentStateChanger(getSupportFragmentManager(), R.id.container);
        backstackDelegate.setStateChanger(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return backstackDelegate.onRetainCustomNonConfigurationInstance();
    }

    @Override
    public void onBackPressed() {
        if(!backstackDelegate.getBackstack().goBack()) {
            super.onBackPressed();
        }
    }

    @Override
    public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
        if(!stateChange.isTopNewStateEqualToPrevious()) {
            fragmentStateChanger.handleStateChange(stateChange);
        }
        completionCallback.stateChangeComplete();
    }

    public Backstack getBackstack() {
        return backstackDelegate.getBackstack();
    }

    @SuppressLint("WrongConstant")
    public static MainActivity get(Context context) {
        // noinspection ResourceType
        return (MainActivity) context.getSystemService(TAG);
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        if(TAG.equals(name)) {
            return this;
        }
        return super.getSystemService(name);
    }
}
