package com.zhuinden.stack_rx_example;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.StateChange;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposables;


/**
 * Created by Owner on 2017. 02. 11..
 */

public class RxStackObservable
        implements ObservableOnSubscribe<StateChange> {
    private Backstack backstack;

    private RxStackObservable(Backstack backstack) {
        this.backstack = backstack;
    }

    public static Observable<StateChange> create(Backstack backstack) {
        return Observable.create(new RxStackObservable(backstack));
    }

    @Override
    public void subscribe(@NonNull ObservableEmitter<StateChange> emitter)
            throws Exception {
        final Backstack.CompletionListener completionListener = stateChange -> {
            if(!emitter.isDisposed()) {
                emitter.onNext(stateChange);
            }
        };
        emitter.setDisposable(Disposables.fromAction(() -> backstack.removeCompletionListener(completionListener)));
        backstack.addCompletionListener(completionListener);
        // no initial value. it's handled by `setStateChanger()`
    }
}
