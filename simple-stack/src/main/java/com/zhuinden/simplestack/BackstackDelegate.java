package com.zhuinden.simplestack;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Owner on 2017. 01. 22..
 */

public class BackstackDelegate {
    private static final String STATES = "STATES";
    
    Backstack backstack;
    
    StateChanger stateChanger;
    
    public BackstackDelegate(StateChanger stateChanger) {
        this.stateChanger = stateChanger;
    }

    Map<Parcelable, SavedState> keyStateMap = new HashMap<>();
    
    public void onCreate(Bundle savedInstanceState, Object nonConfigurationInstance, ArrayList<Parcelable> initialKeys) {
        if(nonConfigurationInstance != null && !(nonConfigurationInstance instanceof NonConfigurationInstance)) {
            throw new IllegalArgumentException("The provided non configuration instance must be of type BackstackDelegate.NonConfigurationInstance!");
        }
        ArrayList<Parcelable> keys;
        if(savedInstanceState != null) {
            keys = savedInstanceState.getParcelableArrayList(Backstack.BACKSTACK);
            List<SavedState> savedStates = savedInstanceState.getParcelableArrayList(STATES);
            if(savedStates != null) {
                for(SavedState savedState : savedStates) {
                    keyStateMap.put(savedState.getKey(), savedState);
                }
            }
        } else {
            keys = initialKeys;
        }
        NonConfigurationInstance nonConfig = (NonConfigurationInstance)nonConfigurationInstance;
        if(nonConfig != null) {
            backstack = nonConfig.getBackstack();
        } else {
            backstack = new Backstack(keys);
        }
        backstack.setStateChanger(stateChanger, Backstack.INITIALIZE);
    }
    
    public NonConfigurationInstance onRetainCustomNonConfigurationInstance() {
        return new NonConfigurationInstance(backstack);
    }
    
    public boolean onBackPressed() {
        return backstack.goBack();
    }

    public void persistViewToState(View view) {
        if(view != null) {
            SparseArray<Parcelable> viewHierarchyState = new SparseArray<>();
            Parcelable key = KeyContextWrapper.getKey(view.getContext());
            if(key == null) {
                throw new IllegalArgumentException("The view [" + view + "] contained no key!");
            }
            view.saveHierarchyState(viewHierarchyState);
            SavedState previousSavedState = SavedState.builder() //
                    .setKey(key) //
                    .setViewHierarchyState(viewHierarchyState) //
                    .build();
            keyStateMap.put(key, previousSavedState);
        }
    }

    public boolean hasSavedState(Parcelable key) {
        return keyStateMap.containsKey(key);
    }

    @NonNull
    public SavedState getSavedState(Parcelable key) {
        if(!keyStateMap.containsKey(key)) {
            throw new IllegalStateException("The state map does not contain key [" + key + "]!");
        }
        return keyStateMap.get(key);
    }
    
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Backstack.BACKSTACK, HistoryBuilder.from(backstack.getHistory()).build());
        outState.putParcelableArrayList(STATES, new ArrayList<>(keyStateMap.values()));
    }
    
    public void onPostResume() {
        if(!backstack.hasStateChanger()) {
            backstack.setStateChanger(stateChanger, Backstack.REATTACH);
        }
    }
    
    public void onPause() {
        if(backstack.hasStateChanger()) {
            backstack.removeStateChanger();
        }
    }

    public boolean isSystemService(String name) {
        return Backstack.BACKSTACK.equals(name);
    }
    
    public Backstack getSystemService(String name) {
        if(isSystemService(name)) {
            return backstack;
        }
        return null;
    }

    public void clearStatesNotIn(List<Parcelable> keys) {
        keyStateMap.keySet().retainAll(keys);
    }

    public static class NonConfigurationInstance {
        private Backstack backstack;
        
        private NonConfigurationInstance(Backstack backstack) {
            this.backstack = backstack;
        }
        
        Backstack getBackstack() {
            return backstack;
        }
    }
}
