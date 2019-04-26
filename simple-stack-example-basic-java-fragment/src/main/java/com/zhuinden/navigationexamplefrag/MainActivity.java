package com.zhuinden.navigationexamplefrag;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;

import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.KeyChange;
import com.zhuinden.simplestack.KeyChanger;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity
        extends AppCompatActivity
        implements KeyChanger {
    private static final String TAG = "MainActivity";

    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    @BindView(R.id.root)
    ViewGroup root;

    BackstackDelegate backstackDelegate;
    FragmentKeyChanger fragmentKeyChanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        backstackDelegate = new BackstackDelegate();
        backstackDelegate.onCreate(savedInstanceState,
                getLastCustomNonConfigurationInstance(),
                History.single(HomeKey.create()));
        backstackDelegate.registerForLifecycleCallbacks(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        navigation.setOnNavigationItemSelectedListener(item -> {
            switch(item.getItemId()) {
                case R.id.navigation_home:
                    replaceHistory(HomeKey.create());
                    return true;
                case R.id.navigation_dashboard:
                    replaceHistory(DashboardKey.create());
                    return true;
                case R.id.navigation_notifications:
                    replaceHistory(NotificationKey.create());
                    return true;
            }
            return false;
        });
        Log.i(TAG, "History [" + Arrays.toString(backstackDelegate.getBackstack().getHistory().toArray()) + "]");
        fragmentKeyChanger = new FragmentKeyChanger(getSupportFragmentManager(), R.id.root);
        backstackDelegate.setKeyChanger(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return backstackDelegate.onRetainCustomNonConfigurationInstance();
    }

    @Override
    public void onBackPressed() {
        if(!backstackDelegate.onBackPressed()) {
            super.onBackPressed();
        }
    }

    private void replaceHistory(Object rootKey) {
        backstackDelegate.getBackstack().setHistory(History.single(rootKey), KeyChange.REPLACE);
    }

    public void navigateTo(Object key) {
        backstackDelegate.getBackstack().goTo(key);
    }

    @SuppressLint("WrongConstant")
    public static MainActivity get(Context context) {
        // noinspection ResourceType
        return (MainActivity)context.getSystemService(TAG);
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        if(TAG.equals(name)) {
            return this;
        }
        return super.getSystemService(name);
    }

    @Override
    public void handleKeyChange(@NonNull KeyChange keyChange, @NonNull Callback completionCallback) {
        if(keyChange.isTopNewKeyEqualToPrevious()) {
            completionCallback.keyChangeComplete();
            return;
        }
        fragmentKeyChanger.handleKeyChange(keyChange);
        completionCallback.keyChangeComplete();
    }
}
