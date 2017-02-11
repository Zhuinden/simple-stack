package com.zhuinden.stack_rx_example;

import android.support.annotation.NonNull;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.StateChange;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * Created by Owner on 2017. 02. 11..
 */

public class RxStackObservable
        implements Observable.OnSubscribe<StateChange> {
    private Backstack backstack;

    private RxStackObservable(Backstack backstack) {
        this.backstack = backstack;
    }

    public static Observable<StateChange> create(Backstack backstack) {
        return Observable.create(new RxStackObservable(backstack));
    }

    @Override
    public void call(Subscriber<? super StateChange> subscriber) {
        final Backstack.CompletionListener completionListener = new Backstack.CompletionListener() {
            @Override
            public void stateChangeCompleted(@NonNull StateChange stateChange) {
                if(!subscriber.isUnsubscribed()) {
                    if(!stateChange.topNewState().equals(stateChange.topPreviousState())) { // distinct top
                        subscriber.onNext(stateChange);
                    }
                }
            }
        };
        subscriber.add(Subscriptions.create(new Action0() {
            @Override
            public void call() {
                backstack.removeCompletionListener(completionListener);
            }
        }));
        backstack.addCompletionListener(completionListener);
        // no initial value. it's handled by `setStateChanger()`
    }
}
