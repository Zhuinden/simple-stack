package com.zhuinden.simplestackdemomultistack.util;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestackdemomultistack.application.Key;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Zhuinden on 2017.02.19..
 */

public class Multistack {
    private Map<String, BackstackDelegate> backstackDelegates = new LinkedHashMap<>();

    public BackstackDelegate add(String identifier, BackstackDelegate backstackDelegate) {
        backstackDelegates.put(identifier, backstackDelegate);
        backstackDelegate.setPersistenceTag(identifier);
        return backstackDelegate;
    }

    public BackstackDelegate get(String identifier) {
        return backstackDelegates.get(identifier);
    }

    public void onCreate(String identifier, Bundle savedInstanceState, NonConfigurationInstance nonConfigurationInstance, Parcelable key) {
        get(identifier).onCreate(savedInstanceState,
                nonConfigurationInstance == null ? null : nonConfigurationInstance.getNonConfigInstance(identifier),
                HistoryBuilder.single(key));
    }

    public void onSaveInstanceState(Bundle outState) {
        for(BackstackDelegate backstackDelegate : backstackDelegates.values()) {
            backstackDelegate.onSaveInstanceState(outState);
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

    public void persistToBackstack(View view) {
        Key key = Backstack.getKey(view.getContext());
        BackstackDelegate backstackDelegate = key.selectDelegate(view.getContext());
        backstackDelegate.persistViewToState(view);
    }


}
