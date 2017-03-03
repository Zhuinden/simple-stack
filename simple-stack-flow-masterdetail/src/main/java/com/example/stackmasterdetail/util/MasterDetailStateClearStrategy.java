package com.example.stackmasterdetail.util;

import android.support.annotation.NonNull;

import com.example.stackmasterdetail.Paths;
import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.simplestack.SavedState;
import com.zhuinden.simplestack.StateChange;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MasterDetailStateClearStrategy
        implements BackstackManager.StateClearStrategy {
    @Override
    public void clearStatesNotIn(@NonNull Map<Object, SavedState> keyStateMap, @NonNull StateChange stateChange) {
        Set<Object> keys = keyStateMap.keySet();
        Iterator<Object> keyIterator = keys.iterator();
        while(keyIterator.hasNext()) {
            Object key = keyIterator.next();
            boolean isMasterOf = false;
            for(Object newKey : stateChange.getNewState()) {
                if(newKey instanceof Paths.MasterDetailPath) {
                    if(key.equals(((Paths.MasterDetailPath) newKey).getMaster())) {
                        isMasterOf = true;
                        break;
                    }
                }
            }
            if(!stateChange.getNewState().contains(key) && !isMasterOf) {
                keyIterator.remove();
            }
        }
    }
}
