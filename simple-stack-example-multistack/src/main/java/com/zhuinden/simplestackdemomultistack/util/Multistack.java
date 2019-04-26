package com.zhuinden.simplestackdemomultistack.util;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.KeyChanger;
import com.zhuinden.simplestackdemomultistack.application.Key;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Zhuinden on 2017.02.19..
 */

public class Multistack {
    private Map<String, BackstackDelegate> backstackDelegates = new LinkedHashMap<>();
    private String selectedStack = null;
    private KeyChanger keyChanger;

    private boolean isPaused = false;

    public BackstackDelegate add(String identifier, BackstackDelegate backstackDelegate) {
        if(selectedStack == null) {
            selectedStack = identifier;
        }
        backstackDelegates.put(identifier, backstackDelegate);
        backstackDelegate.setPersistenceTag(identifier);
        return backstackDelegate;
    }

    public BackstackDelegate get(String identifier) {
        return backstackDelegates.get(identifier);
    }

    public void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            selectedStack = savedInstanceState.getString("selectedStack");
        }
    }

    public void onCreate(String identifier, Bundle savedInstanceState, NonConfigurationInstance nonConfigurationInstance, Parcelable key) {
        get(identifier).onCreate(savedInstanceState,
                nonConfigurationInstance == null ? null : nonConfigurationInstance.getNonConfigInstance(identifier),
                History.single(key));
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putString("selectedStack", selectedStack);
        for(BackstackDelegate backstackDelegate : backstackDelegates.values()) {
            backstackDelegate.onSaveInstanceState(outState);
        }
    }

    public Object onRetainCustomNonConfigurationInstance() {
        Multistack.NonConfigurationInstance nonConfigurationInstance = new NonConfigurationInstance();
        for(Map.Entry<String, BackstackDelegate> entry : backstackDelegates.entrySet()) {
            nonConfigurationInstance.putNonConfigInstance(entry.getKey(), entry.getValue().onRetainCustomNonConfigurationInstance());
        }
        return nonConfigurationInstance;
    }

    public void setKeyChanger(KeyChanger keyChanger) {
        this.keyChanger = keyChanger;
        for(Map.Entry<String, BackstackDelegate> entry : backstackDelegates.entrySet()) {
            if(!entry.getKey().equals(selectedStack)) {
                entry.getValue().onPause(); // FIXME maybe this should be exposed better.
            } else {
                entry.getValue().setKeyChanger(keyChanger);
            }
        }
    }

    public void onPostResume() {
        isPaused = false;
        for(Map.Entry<String, BackstackDelegate> entry : backstackDelegates.entrySet()) {
            if(!entry.getKey().equals(selectedStack)) {
                entry.getValue().onPause(); // FIXME maybe this should be exposed better.
            } else {
                entry.getValue().onPostResume();
            }
        }
    }

    public void onPause() {
        isPaused = true;
        for(Map.Entry<String, BackstackDelegate> entry : backstackDelegates.entrySet()) {
            entry.getValue().onPause();
        }
    }

    public boolean onBackPressed() {
        return get(selectedStack).onBackPressed();
    }

    public void onDestroy() {
        for(Map.Entry<String, BackstackDelegate> entry : backstackDelegates.entrySet()) {
            entry.getValue().onDestroy();
        }
    }

    public void setSelectedStack(String identifier) {
        if(!backstackDelegates.containsKey(identifier)) {
            throw new IllegalArgumentException("You cannot specify a stack [" + identifier + "] that does not exist!");
        }
        if(!selectedStack.equals(identifier)) {
            this.selectedStack = identifier;
            setKeyChanger(keyChanger);
        }
    }

    public static class NonConfigurationInstance {
        Map<String, BackstackDelegate.NonConfigurationInstance> nonConfigInstances = new HashMap<>();

        public BackstackDelegate.NonConfigurationInstance getNonConfigInstance(String key) {
            return nonConfigInstances.get(key);
        }

        public void putNonConfigInstance(String key, BackstackDelegate.NonConfigurationInstance nonConfigurationInstance) {
            nonConfigInstances.put(key, nonConfigurationInstance);
        }
    }

    public void persistViewToState(View view) {
        if(view != null) {
            Key key = Backstack.getKey(view.getContext());
            BackstackDelegate backstackDelegate = key.selectDelegate(view.getContext());
            backstackDelegate.persistViewToState(view);
        }
    }

    public void restoreViewFromState(View view) {
        Key key = Backstack.getKey(view.getContext());
        BackstackDelegate backstackDelegate = key.selectDelegate(view.getContext());
        backstackDelegate.restoreViewFromState(view);
    }
}
