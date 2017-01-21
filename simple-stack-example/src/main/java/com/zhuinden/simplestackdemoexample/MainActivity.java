package com.zhuinden.simplestackdemoexample;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
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

    private void persistViewToState(View view) {
        if(view != null) {
            SparseArray<Parcelable> viewHierarchyState = new SparseArray<>();
            Key key = KeyContextWrapper.getKey(view.getContext());
            view.saveHierarchyState(viewHierarchyState);
            SavedState previousSavedState = SavedState.builder() //
                    .setKey(key) //
                    .setViewHierarchyState(viewHierarchyState) //
                    .build();
            keyStateMap.put(key, previousSavedState);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        persistViewToState(root.getChildAt(0));
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
        persistViewToState(root.getChildAt(0));
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
