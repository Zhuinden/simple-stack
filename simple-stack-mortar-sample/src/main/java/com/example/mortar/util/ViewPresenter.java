package com.example.mortar.util;

import android.os.Bundle;

import com.zhuinden.servicetree.ServiceTree;

/**
 * Created by Zhuinden on 2017.05.12..
 */

public abstract class ViewPresenter<V> implements ServiceTree.Scoped {
    V view;

    public void onLoad(Bundle bundle) {

    }

    public void onSave(Bundle bundle) {

    }

    public final boolean hasView() {
        return view != null;
    }

    public void takeView(V view) {
        this.view = view;
        onLoad(new Bundle()); // TODO
    }

    public void dropView(V view) {
        onSave(new Bundle()); // TODO
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
