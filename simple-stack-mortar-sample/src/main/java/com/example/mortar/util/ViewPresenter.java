package com.example.mortar.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.statebundle.StateBundle;

/**
 * Created by Zhuinden on 2017.05.12..
 */

public abstract class ViewPresenter<V>
        implements ServiceTree.Scoped, Bundleable {
    StateBundle stateBundle; // <-- imitating "Mortar.ViewPresenter" behavior
    // where onLoad() is called on both bundle service runner onLoad() AND takeView()

    V view;

    public void onLoad(@Nullable StateBundle bundle) {

    }

    public void onSave(@NonNull StateBundle bundle) {

    }

    @Override
    public final void fromBundle(@Nullable StateBundle bundle) {
        this.stateBundle = bundle;
        onLoad(bundle);
    }

    @NonNull
    @Override
    public final StateBundle toBundle() {
        StateBundle stateBundle = new StateBundle();
        onSave(stateBundle);
        this.stateBundle = new StateBundle(stateBundle);
        return stateBundle;
    }

    public final boolean hasView() {
        return view != null;
    }

    public void takeView(V view) {
        this.view = view;
        onLoad(this.stateBundle);
    }

    public void dropView(V view) {
        this.stateBundle = toBundle(); // make sure ViewPresenter state is not stale on back navigation:
        // this is what Mortar never bothered to fix and it was super-clunky

        this.view = null;
    }

    public V getView() {
        return view;
    }

    @Override
    public void onEnterScope(ServiceTree.Node node) {

    }

    @Override
    public void onExitScope() {
    }
}
