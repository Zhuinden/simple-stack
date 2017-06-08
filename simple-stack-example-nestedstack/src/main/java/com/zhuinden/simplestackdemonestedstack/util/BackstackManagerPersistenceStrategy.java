package com.zhuinden.simplestackdemonestedstack.util;

import android.support.annotation.NonNull;
import android.view.View;

import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.simplestack.navigator.DefaultStateChanger;

/**
 * Created by Owner on 2017. 06. 08..
 */

public class BackstackManagerPersistenceStrategy
        implements DefaultStateChanger.StatePersistenceStrategy {
    private final BackstackManager backstackManager;

    public BackstackManagerPersistenceStrategy(BackstackManager backstackManager) {
        this.backstackManager = backstackManager;
    }

    @Override
    public void persistViewToState(@NonNull Object previousKey, @NonNull View previousView) {
        backstackManager.persistViewToState(previousView);
    }

    @Override
    public void restoreViewFromState(@NonNull Object newKey, @NonNull View newView) {
        backstackManager.restoreViewFromState(newView);
    }
}
