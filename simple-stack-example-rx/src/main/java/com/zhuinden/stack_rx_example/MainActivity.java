package com.zhuinden.stack_rx_example;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestack.navigator.DefaultStateChanger;
import com.zhuinden.simplestack.navigator.Navigator;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

public class MainActivity
        extends AppCompatActivity {
    @BindView(R.id.root)
    FrameLayout root;

    Subscription subscription;

    DefaultStateChanger defaultStateChanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        defaultStateChanger = DefaultStateChanger.create(this, root);
        Backstack backstack = Navigator.configure().setStateChanger(new NoOpStateChanger()).setDeferredInitialization(true).install(this, root, HistoryBuilder.single(FirstKey.create()));
        subscription = RxStackObservable.create(backstack).subscribe(stateChange -> {
            defaultStateChanger.performViewChange(stateChange.topPreviousState(), stateChange.topNewState(), stateChange, () -> {
            });
        });
        Navigator.executeDeferredInitialization(this);
    }

    @Override
    public void onBackPressed() {
        if(!Navigator.onBackPressed(this)) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if(subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
        super.onDestroy();
    }

    @Override
    public Object getSystemService(String name) {
        if(BackstackService.TAG.equals(name)) {
            return Navigator.getBackstack(this);
        }
        return super.getSystemService(name);
    }
}