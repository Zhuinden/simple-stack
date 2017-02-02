package com.zhuinden.simplestackdemoexamplefragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

/**
 * Created by Zhuinden on 2017.02.01..
 */

public class MainActivity
        extends AppCompatActivity
        implements StateChanger {
    private static final String TAG = "MainActivity";

    BackstackDelegate backstackDelegate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    public Object getSystemService(String name) {
        if(BackstackService.TAG.equals(name)) {
            return backstackDelegate.getBackstack();
        }
        return super.getSystemService(name);
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            completionCallback.stateChangeComplete();
            return;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().disallowAddToBackStack();
        if(stateChange.getDirection() == StateChange.FORWARD) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        } else if(stateChange.getDirection() == StateChange.BACKWARD) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        }

        for(Parcelable _oldKey : stateChange.getPreviousState()) {
            Key oldKey = (Key) _oldKey;
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(oldKey.getFragmentTag());
            if(fragment != null) {
                if(!stateChange.getNewState().contains(oldKey)) {
                    Log.i(TAG, "Old key is NOT in new state: removing [" + oldKey + "]");
                    fragmentTransaction.remove(fragment);
                } else if(!fragment.isDetached()) {
                    Log.i(TAG, "Old key is in new state, but not showing: detaching [" + oldKey + "]");
                    fragmentTransaction.detach(fragment);
                }
            }
        }
        for(Parcelable _newKey : stateChange.getNewState()) {
            Key newKey = (Key) _newKey;
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(newKey.getFragmentTag());
            if(newKey.equals(stateChange.topNewState())) {
                if(fragment != null) {
                    if(fragment.isDetached()) {
                        Log.i(TAG, "New key is top state but detached: reattaching [" + newKey + "]");
                        fragmentTransaction.attach(fragment);
                    } else {
                        Log.i(TAG, "New key is top state but already attached: probably config change for [" + newKey + "]");
                    }
                } else {
                    Log.i(TAG, "New fragment does not exist yet, adding [" + newKey + "]");
                    fragment = newKey.createFragment();
                    fragmentTransaction.add(R.id.root, fragment, newKey.getFragmentTag());
                }
            } else {
                if(fragment != null && !fragment.isDetached()) {
                    Log.i(TAG, "New fragment is not active fragment. It should be detached: [" + newKey + "]");
                    fragmentTransaction.detach(fragment);
                } else {
                    Log.i(TAG, "New fragment is already detached or doesn't exist, as expected: [" + newKey + "]");
                }
            }
        }
        fragmentTransaction.commitNow();
        completionCallback.stateChangeComplete();
    }
}
