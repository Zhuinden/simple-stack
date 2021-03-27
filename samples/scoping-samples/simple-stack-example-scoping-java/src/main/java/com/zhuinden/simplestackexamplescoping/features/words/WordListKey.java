package com.zhuinden.simplestackexamplescoping.features.words;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.ServiceBinder;
import com.zhuinden.simplestackexamplescoping.core.navigation.BaseKey;

import javax.annotation.Nonnull;

import androidx.fragment.app.Fragment;

@AutoValue
public abstract class WordListKey
        extends BaseKey {
    public static WordListKey create() {
        return new AutoValue_WordListKey();
    }

    @Nonnull
    @Override
    protected Fragment instantiateFragment() {
        return new WordListFragment();
    }

    @Override
    public void bindServices(@Nonnull ServiceBinder serviceBinder) {
        final Backstack backstack = serviceBinder.getBackstack();

        WordController wordController = new WordController(backstack);

        serviceBinder.addService(WordController.class.getName(), wordController);
        serviceBinder.addAlias(WordListFragment.DataProvider.class.getName(), wordController);
        serviceBinder.addAlias(WordListFragment.ActionHandler.class.getName(), wordController);
        serviceBinder.addAlias(NewWordFragment.ActionHandler.class.getName(), wordController);
        serviceBinder.addService("WordEventEmitter", wordController.getEventEmitter());
    }
}
