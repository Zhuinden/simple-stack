package com.zhuinden.simplestackexamplescoping.features.words;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.zhuinden.eventemitter.EventEmitter;
import com.zhuinden.eventemitter.EventSource;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.statebundle.StateBundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.reactivex.Observable;

public class WordController
        implements NewWordFragment.ActionHandler, WordListFragment.ActionHandler, WordListFragment.DataProvider, Bundleable {
    private final BehaviorRelay<List<String>> words = BehaviorRelay.createDefault(Arrays.asList("Bogus",
                                                                                                "Magic",
                                                                                                "Scoping mechanisms"));
    private final EventEmitter<Events> wordEventEmitter = new EventEmitter<>();

    private final Backstack backstack;


    public WordController(Backstack backstack) {
        this.backstack = backstack;
    }

    public EventSource<Events> getEventEmitter() {
        return wordEventEmitter;
    }

    @Nonnull
    @Override
    public StateBundle toBundle() {
        StateBundle stateBundle = new StateBundle();
        //noinspection ConstantConditions
        stateBundle.putStringArrayList("words", new ArrayList<String>(words.getValue()));
        return stateBundle;
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {
        if(bundle != null) {
            words.accept(Collections.unmodifiableList(bundle.getStringArrayList("words")));
        }
    }

    @Override
    public void onAddWordClicked(String word) {
        if(word.isEmpty()) {
            return;
        }
        //noinspection ConstantConditions
        List<String> list = new ArrayList<>(words.getValue());
        list.add(word);
        words.accept(Collections.unmodifiableList(list));
        wordEventEmitter.emit(new Events.NewWordAdded(word));
        backstack.goBack();
    }

    @Override
    public void onAddNewWordClicked() {
        backstack.goTo(NewWordKey.create());
    }

    @Override
    public Observable<List<String>> getWordList() {
        return words;
    }

    public static class Events {
        public static class NewWordAdded
                extends Events {
            private final String word;

            public NewWordAdded(String word) {
                this.word = word;
            }

            public final String getWord() {
                return word;
            }
        }
    }
}
