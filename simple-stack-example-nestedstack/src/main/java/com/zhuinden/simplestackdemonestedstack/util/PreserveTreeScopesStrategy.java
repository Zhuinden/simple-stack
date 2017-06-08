package com.zhuinden.simplestackdemonestedstack.util;

import android.support.annotation.NonNull;
import android.util.Log;

import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.simplestack.SavedState;
import com.zhuinden.simplestack.StateChange;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by Zhuinden on 2017.03.28..
 */
public class PreserveTreeScopesStrategy
        implements BackstackManager.StateClearStrategy {
    private ServiceTree serviceTree;

    public PreserveTreeScopesStrategy(ServiceTree serviceTree) {
        this.serviceTree = serviceTree;
    }

    @Override
    public void clearStatesNotIn(@NonNull Map<Object, SavedState> keyStateMap, @NonNull StateChange stateChange) {
        Log.i("SCOPES", Arrays.toString(serviceTree.getKeys().toArray()));
        keyStateMap.keySet().retainAll(serviceTree.getKeys());
    }
}
