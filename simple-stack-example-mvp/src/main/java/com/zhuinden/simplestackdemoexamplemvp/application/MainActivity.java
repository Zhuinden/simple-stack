package com.zhuinden.simplestackdemoexamplemvp.application;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.squareup.coordinators.Coordinator;
import com.squareup.coordinators.CoordinatorProvider;
import com.squareup.coordinators.Coordinators;
import com.transitionseverywhere.TransitionManager;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.data.manager.DatabaseManager;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.first.FirstKey;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.statistics.StatisticsKey;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks.TasksKey;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity
        extends AppCompatActivity
        implements StateChanger {
    public interface OptionsItemSelectedListener {
        boolean onOptionsItemSelected(MenuItem menuItem);
    }

    public static final String TAG = "MainActivity";

    public static MainActivity get(Context context) {
        // noinspection ResourceType
        return (MainActivity)context.getSystemService(TAG);
    }

    @BindView(R.id.root)
    RelativeLayout root;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.fab_add_task)
    FloatingActionButton fabAddTask;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(root != null && root.getChildAt(0) != null) {
            boolean handled = false;
            if(root.getChildAt(0) instanceof OptionsItemSelectedListener) {
                handled = ((OptionsItemSelectedListener)(root.getChildAt(0))).onOptionsItemSelected(item);
            }
            if(handled) {
                return handled;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(root != null && root.getChildAt(0) != null) {
            Key key = Backstack.getKey(root.getChildAt(0).getContext());
            getMenuInflater().inflate(key.menu(), menu);
            return true;
        }
        return false;
    }

    private final NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = (NavigationView.OnNavigationItemSelectedListener) item -> {
        switch (item.getItemId()) {
            case R.id.list_navigation_menu_item:
                Backstack.get(MainActivity.this).goTo(TasksKey.create());
                break;
            case R.id.statistics_navigation_menu_item:
                Backstack.get(MainActivity.this).goTo(StatisticsKey.create());
            default:
                break;
        }
        setCheckedItem(item.getItemId());
        // Close the navigation drawer when an item is selected.
        drawerLayout.closeDrawers();
        return true;
    };

    private void setCheckedItem(int navigationItemId) {
        Menu menu = navigationView.getMenu();
        for(int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            menuItem.setChecked(menuItem.getItemId() == navigationItemId);
        }
    }

    private ActionBarDrawerToggle drawerToggle;

    private void setupViewsForKey(Key key) {
        if(key.shouldShowUp()) {
            drawerToggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            drawerToggle.setDrawerIndicatorEnabled(true);
        }
        drawerToggle.syncState();
        setCheckedItem(key.navigationViewId());
        //supportInvalidateOptionsMenu();
        if(key.isFabVisible()) {
            fabAddTask.setVisibility(View.VISIBLE);
        } else {
            fabAddTask.setVisibility(View.GONE);
        }
    }

    BackstackDelegate backstackDelegate;

    @Inject
    DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                supportInvalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Backstack.get(MainActivity.this).goBack();
            }
        });
        //actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(true);

        CustomApplication.get(this).getComponent().inject(this);
        databaseManager.init(this);

        MainScopeListener mainScopeListener = (MainScopeListener)getSupportFragmentManager().findFragmentByTag("MAIN_SCOPE_LISTENER");
        if(mainScopeListener == null) {
            mainScopeListener = new MainScopeListener();
            getSupportFragmentManager().beginTransaction().add(mainScopeListener, "MAIN_SCOPE_LISTENER").commit();
        }
        CustomApplication.get(this).getComponent().inject(mainScopeListener);

        Coordinators.installBinder(root, new CoordinatorProvider() {
            @Nullable
            @Override
            public Coordinator provideCoordinator(View view) {
                Log.i(TAG, "Providing coordinator for [" + view + "]");
                Key key = Backstack.getKey(view.getContext());
                return key.newCoordinator(); // maybe should be obtained from a component
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        backstackDelegate = new BackstackDelegate(this);
        backstackDelegate.onCreate(savedInstanceState, //
                getLastCustomNonConfigurationInstance(), //
                HistoryBuilder.single(FirstKey.create()));
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
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
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }
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
        backstackDelegate.persistViewToState(root.getChildAt(0));
        backstackDelegate.onSaveInstanceState(outState);
    }

    @Override
    public Object getSystemService(String name) {
        if(TAG.equals(name)) {
            return this;
        }
        if(backstackDelegate != null && backstackDelegate.isSystemService(name)) {
            return backstackDelegate.getSystemService(name);
        }
        return super.getSystemService(name);
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            // no-op
            completionCallback.stateChangeComplete();
            return;
        }
        TransitionManager.beginDelayedTransition(root);
        Log.i(TAG, "Persisting view state of [" + root.getChildAt(0) + "]");
        backstackDelegate.persistViewToState(root.getChildAt(0));
        root.removeAllViews();
        Key newKey = stateChange.topNewState();
        Context newContext = stateChange.createContext(this, newKey);
        View view = LayoutInflater.from(newContext).inflate(newKey.layout(), root, false);
        Log.i(TAG, "Adding view [" + view + "]");
        root.addView(view);
        Log.i(TAG, "Restoring view state of [" + view + "]");
        backstackDelegate.restoreViewFromState(view);
        backstackDelegate.clearStatesNotIn(stateChange.getNewState());

        setupViewsForKey(newKey);
        completionCallback.stateChangeComplete();
    }
}
