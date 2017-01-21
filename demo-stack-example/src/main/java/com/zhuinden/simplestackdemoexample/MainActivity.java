package com.zhuinden.simplestackdemoexample;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.zhuinden.simplestackdemo.stack.Backstack;
import com.zhuinden.simplestackdemo.stack.HistoryBuilder;
import com.zhuinden.simplestackdemo.stack.StateChange;
import com.zhuinden.simplestackdemo.stack.StateChanger;
import com.zhuinden.simplestackdemoexample.demo.FirstKey;
import com.zhuinden.simplestackdemoexample.demo.Key;
import com.zhuinden.simplestackdemoexample.demo.KeyContextWrapper;
import com.zhuinden.simplestackdemoexample.demo.SavedState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity
        extends AppCompatActivity
        implements StateChanger {
    public static final String BACKSTACK = "BACKSTACK";
    public static final String STATES = "STATES";

    @BindView(R.id.root)
    RelativeLayout root;

    Backstack backstack;

    Map<Key, SavedState> keyStateMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ArrayList<Parcelable> keys;
        if(savedInstanceState != null) {
            keys = savedInstanceState.getParcelableArrayList(BACKSTACK);
            List<SavedState> savedStates = savedInstanceState.getParcelableArrayList(STATES);
            if(savedStates != null) {
                for(SavedState savedState : savedStates) {
                    keyStateMap.put(savedState.getKey(), savedState);
                }
            }
        } else {
            keys = HistoryBuilder.single(new FirstKey());
        }
        backstack = (Backstack) getLastCustomNonConfigurationInstance();
        if(backstack == null) {
            backstack = new Backstack(keys);
        }
        backstack.setStateChanger(this, Backstack.INITIALIZE);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return backstack;
    }

    @Override
    public void onBackPressed() {
        if(!backstack.goBack()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(root != null) {
            SparseArray<Parcelable> viewHierarchyState = new SparseArray<>();
            root.getChildAt(0).saveHierarchyState(viewHierarchyState);
            Key currentKey = KeyContextWrapper.getKey(root.getChildAt(0).getContext());
            SavedState currentSavedState = SavedState.builder()
                    .setKey(currentKey)
                    .setViewHierarchyState(viewHierarchyState)
                    .build();
            keyStateMap.put(currentKey, currentSavedState);
        }
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BACKSTACK, HistoryBuilder.from(backstack.getHistory()).build());
        outState.putParcelableArrayList(STATES, new ArrayList<>(keyStateMap.values()));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(!backstack.hasStateChanger()) {
            backstack.setStateChanger(this, Backstack.REATTACH);
        }
    }

    @Override
    protected void onPause() {
        if(backstack.hasStateChanger()) {
            backstack.removeStateChanger();
        }
        super.onPause();
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            // no-op
            completionCallback.stateChangeComplete();
            return;
        }
        if(stateChange.topPreviousState() != null) {
            SparseArray<Parcelable> viewHierarchyState = new SparseArray<>();
            Key previousKey = stateChange.topPreviousState();
            root.getChildAt(0).saveHierarchyState(viewHierarchyState);
            SavedState previousSavedState = SavedState.builder()
                    .setKey(previousKey)
                    .setViewHierarchyState(viewHierarchyState)
                    .build();
            keyStateMap.put(previousKey, previousSavedState);
        }
        root.removeAllViews();
        Key newKey = stateChange.topNewState();
        Context newContext = new KeyContextWrapper(this, newKey);
        View view = LayoutInflater.from(newContext).inflate(newKey.layout(), root, false);
        if(keyStateMap.containsKey(newKey)) {
            SavedState savedState = keyStateMap.get(newKey);
            view.restoreHierarchyState(savedState.getViewHierarchyState());
        }
        root.addView(view);
        keyStateMap.keySet().retainAll(stateChange.getNewState());
        completionCallback.stateChangeComplete();
    }

    @Override
    public Object getSystemService(String name) {
        if(BACKSTACK.equals(name)) {
            return backstack;
        }
        return super.getSystemService(name);
    }
}
