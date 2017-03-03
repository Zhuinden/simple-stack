package com.zhuinden.simplestackdemonestedstack.presentation.paths.main;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.simplestackdemonestedstack.R;
import com.zhuinden.simplestackdemonestedstack.application.Key;
import com.zhuinden.simplestackdemonestedstack.util.Composite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Zhuinden on 2017.02.26..
 */
@AutoValue
public abstract class MainKey
        extends Key
        implements Composite {
    @Override
    public int layout() {
        return R.layout.path_main;
    }

    @Override
    public String stackIdentifier() {
        return "";
    }

    public static MainKey create() {
        return new AutoValue_MainKey();
    }

    @Override
    public List<?> keys() {
        MainView.StackType[] stackTypes = MainView.StackType.values();
        List<Key> list = new ArrayList<>(stackTypes.length);
        for(MainView.StackType stackType : stackTypes) {
            list.add(stackType.getKey());
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public boolean hasNestedStack() {
        return true;
    }

    @Override
    protected List<?> initialKeys() {
        return Arrays.asList(MainView.StackType.values()[0].getKey());
    }

    @Override
    protected BackstackManager createBackstackManager() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setStateClearStrategy((keyStateMap, stateChange) -> {
            // do nothing
        });
        return backstackManager;
    }
}
