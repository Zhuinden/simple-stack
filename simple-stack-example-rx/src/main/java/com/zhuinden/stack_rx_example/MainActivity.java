package com.zhuinden.stack_rx_example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.navigator.DefaultStateChanger;
import com.zhuinden.simplestack.navigator.Navigator;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

public class MainActivity
        extends AppCompatActivity {
    @BindView(R.id.root)
    FrameLayout root;

    Disposable disposable;

    DefaultStateChanger defaultStateChanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        defaultStateChanger = DefaultStateChanger.create(this, root);
        Backstack backstack = Navigator.configure()
                .setStateChanger(new NoOpStateChanger())
                .setDeferredInitialization(true)
                .install(this, root, HistoryBuilder.single(FirstKey.create()));
        disposable = RxStackObservable.create(backstack).subscribe(stateChange -> {
            if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
                return;
            }
            defaultStateChanger.performViewChange(stateChange.topPreviousState(),
                    stateChange.topNewState(),
                    stateChange,
                    () -> { // no callback to wait for with Rx
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
        if(disposable != null) {
            disposable.dispose();
            disposable = null;
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