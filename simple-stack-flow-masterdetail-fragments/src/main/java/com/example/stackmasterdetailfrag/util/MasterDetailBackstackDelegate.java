package com.example.stackmasterdetailfrag.util;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.stackmasterdetailfrag.Paths;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.SavedState;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Owner on 2017. 02. 13..
 */

public class MasterDetailBackstackDelegate
        extends BackstackDelegate {
    public MasterDetailBackstackDelegate(@Nullable StateChanger stateChanger) {
        super(stateChanger);
    }

    @Override
    protected void clearStatesNotIn(@NonNull Map<Parcelable, SavedState> keyStateMap, @NonNull StateChange stateChange) {
        Set<Parcelable> keys = keyStateMap.keySet();
        Iterator<Parcelable> keyIterator = keys.iterator();
        while(keyIterator.hasNext()) {
            Parcelable key = keyIterator.next();
            boolean isMasterOf = false;
            for(Parcelable newKey : stateChange.getNewState()) {
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
