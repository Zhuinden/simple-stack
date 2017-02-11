package com.zhuinden.stack_rx_example;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.HistoryBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

public class MainActivity
        extends AppCompatActivity {
    @BindView(R.id.root)
    FrameLayout root;

    BackstackDelegate backstackDelegate;

    Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        backstackDelegate = new BackstackDelegate(null);
        backstackDelegate.onCreate(savedInstanceState, //
                getLastCustomNonConfigurationInstance(), //
                HistoryBuilder.single(FirstKey.create()));
        subscription = RxStackObservable.create(backstackDelegate.getBackstack()).subscribe(stateChange -> {
            backstackDelegate.persistViewToState(root.getChildAt(0));
            root.removeAllViews();
            Key newKey = stateChange.topNewState();
            Context newContext = stateChange.createContext(MainActivity.this, newKey);
            View view = LayoutInflater.from(newContext).inflate(newKey.layout(), root, false);
            backstackDelegate.restoreViewFromState(view);
            root.addView(view);
        });
        backstackDelegate.setStateChanger(new NoOpStateChanger());
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return backstackDelegate.onRetainCustomNonConfigurationInstance();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        backstackDelegate.onPostResume();
    }

    @Override
    public void onBackPressed() {
        if(!backstackDelegate.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        backstackDelegate.onPause();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        backstackDelegate.persistViewToState(root.getChildAt(0)); // <-- persisting view state
        backstackDelegate.onSaveInstanceState(outState); // <-- persisting backstack + view states
    }

    @Override
    protected void onDestroy() {
        if(subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
        backstackDelegate.onDestroy(); // <-- very important!
        super.onDestroy();
    }

    @Override
    public Object getSystemService(String name) {
        if(BackstackService.TAG.equals(name)) {
            return backstackDelegate.getBackstack();
        }
        return super.getSystemService(name);
    }
}