package com.example.stackmasterdetailfrag.util;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.stackmasterdetailfrag.Paths;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.KeyParceler;
import com.zhuinden.simplestack.ServiceFactory;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Owner on 2017. 02. 13..
 */

public class MasterDetailBackstackDelegate
        extends BackstackDelegate {
    public MasterDetailBackstackDelegate(@Nullable StateChanger stateChanger, @NonNull List<ServiceFactory> servicesFactories, @NonNull Map<String, Object> rootServices, KeyParceler keyParceler) {
        super(stateChanger, servicesFactories, rootServices, keyParceler);
    }

    @Override
    protected Collection<? extends Parcelable> getAdditionalRetainedKeys(@NonNull StateChange stateChange) {
        Set<Parcelable> keys = new HashSet<>();
        for(Object key : stateChange.getNewState()) {
            if(key instanceof Paths.MasterDetailPath) {
                keys.add(((Paths.MasterDetailPath) key).getMaster());
            }
        }
        return keys;
    }
}
