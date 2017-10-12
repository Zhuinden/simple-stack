package com.zhuinden.demo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestackdemoexamplefragments.R;

import java.util.List;

/**
 * Created by Zhuinden on 2017.02.01..
 */

public class DemoActivity
        extends AppCompatActivity
        implements StateChanger {
    private static final String TAG = "DemoActivity";

    BackstackDelegate backstackDelegate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity);
        backstackDelegate = new BackstackDelegate(null);
        backstackDelegate.onCreate(savedInstanceState, getLastCustomNonConfigurationInstance(), HistoryBuilder.single(FirstKey.create()));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        backstackDelegate.setStateChanger(this);
    }

    @Override
    protected void onPostResume() {
        backstackDelegate.onPostResume();
        super.onPostResume();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return backstackDelegate.onRetainCustomNonConfigurationInstance();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        backstackDelegate.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        backstackDelegate.onPause();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(!backstackDelegate.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        backstackDelegate.onDestroy();
        super.onDestroy();
    }

    @Override
    public Object getSystemService(String name) {
        if(BackstackService.TAG.equals(name)) {
            return backstackDelegate.getBackstack();
        }
        return super.getSystemService(name);
    }

    @Override
    public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            completionCallback.stateChangeComplete();
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().disallowAddToBackStack();
        if(stateChange.getDirection() == StateChange.FORWARD) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        } else if(stateChange.getDirection() == StateChange.BACKWARD) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        }

        List<Key> previousState = stateChange.getPreviousState();
        List<Key> newState = stateChange.getNewState();
        for(Key oldKey : previousState) {
            Fragment fragment = fragmentManager.findFragmentByTag(oldKey.getFragmentTag());
            if(fragment != null) {
                if(!newState.contains(oldKey)) {
                    fragmentTransaction.remove(fragment);
                } else if(!fragment.isDetached()) {
                    fragmentTransaction.detach(fragment);
                }
            }
        }
        for(Key newKey : newState) {
            Fragment fragment = fragmentManager.findFragmentByTag(newKey.getFragmentTag());
            if(newKey.equals(stateChange.topNewState())) {
                if(fragment != null) {
                    if(fragment.isDetached()) {
                        fragmentTransaction.attach(fragment);
                    }
                } else {
                    fragment = newKey.createFragment();
                    fragmentTransaction.add(R.id.root, fragment, newKey.getFragmentTag());
                }
            } else {
                if(fragment != null && !fragment.isDetached()) {
                    fragmentTransaction.detach(fragment);
                }
            }
        }
        fragmentTransaction.commitNow();
        completionCallback.stateChangeComplete();
    }
}
