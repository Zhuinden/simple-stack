package com.example.fragmenttransitions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.KeyChange;
import com.zhuinden.simplestack.KeyChanger;

/**
 * Main activity that holds our fragments
 *
 * @author bherbst
 */
public class MainActivity
        extends AppCompatActivity
        implements KeyChanger {
    static final String TAG = "MainActivity";

    BackstackDelegate backstackDelegate;
    FragmentKeyChanger fragmentKeyChanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        backstackDelegate = new BackstackDelegate();
        backstackDelegate.onCreate(savedInstanceState, getLastCustomNonConfigurationInstance(), History.single(GridKey.create()));
        backstackDelegate.registerForLifecycleCallbacks(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentKeyChanger = new FragmentKeyChanger(getSupportFragmentManager(), R.id.container);
        backstackDelegate.setKeyChanger(this);
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
    public void handleKeyChange(@NonNull KeyChange keyChange, @NonNull Callback completionCallback) {
        if(!keyChange.isTopNewKeyEqualToPrevious()) {
            fragmentKeyChanger.handleKeyChange(keyChange);
        }
        completionCallback.keyChangeComplete();
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
