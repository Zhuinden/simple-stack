package com.example.stackmasterdetail.util;

import android.support.annotation.NonNull;

import com.example.stackmasterdetail.paths.MasterDetailPath;
import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.simplestack.SavedState;
import com.zhuinden.simplestack.StateChange;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MasterDetailStateClearStrategy
        implements BackstackManager.StateClearStrategy {
    @Override
    public void clearStatesNotIn(@NonNull Map<Object, SavedState> keyStateMap, @NonNull StateChange stateChange) {
        Set<Object> keys = keyStateMap.keySet();
        Iterator<Object> keyIterator = keys.iterator();
        List<Object> newState = stateChange.getNewState();
        while(keyIterator.hasNext()) {
            Object key = keyIterator.next();
            boolean isMasterOf = false;
            for(Object newKey : newState) {
                if(newKey instanceof MasterDetailPath) {
                    if(key.equals(((MasterDetailPath) newKey).getMaster())) {
                        isMasterOf = true;
                        break;
                    }
                }
            }
            if(!newState.contains(key) && !isMasterOf) {
                keyIterator.remove();
            }
        }
    }
}
