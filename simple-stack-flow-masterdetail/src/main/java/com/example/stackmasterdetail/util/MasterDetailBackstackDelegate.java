package com.example.stackmasterdetail.util;

import android.support.annotation.Nullable;

import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.StateChanger;

/**
 * Created by Owner on 2017. 02. 13..
 */

public class MasterDetailBackstackDelegate
        extends BackstackDelegate {
    public MasterDetailBackstackDelegate(@Nullable StateChanger stateChanger) {
        super(stateChanger);
    }
// FIXME moved to backstack manager
//    @Override
//    protected void clearStatesNotIn(@NonNull Map<Object, SavedState> keyStateMap, @NonNull StateChange stateChange) {
//        Set<Object> keys = keyStateMap.keySet();
//        Iterator<Object> keyIterator = keys.iterator();
//        while(keyIterator.hasNext()) {
//            Object key = keyIterator.next();
//            boolean isMasterOf = false;
//            for(Object newKey : stateChange.getNewState()) {
//                if(newKey instanceof Paths.MasterDetailPath) {
//                    if(key.equals(((Paths.MasterDetailPath) newKey).getMaster())) {
//                        isMasterOf = true;
//                        break;
//                    }
//                }
//            }
//            if(!stateChange.getNewState().contains(key) && !isMasterOf) {
//                keyIterator.remove();
//            }
//        }
//    }
}
