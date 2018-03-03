package com.zhuinden.simplestackexamplemvvm.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestackexamplemvvm.R;
import com.zhuinden.simplestackexamplemvvm.application.injection.Injection;
import com.zhuinden.simplestackexamplemvvm.core.viewmodels.ViewModelLifecycleHelper;
import com.zhuinden.simplestackexamplemvvm.presentation.paths.statistics.StatisticsKey;
import com.zhuinden.simplestackexamplemvvm.presentation.paths.tasks.TasksKey;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v4.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
import static android.support.v4.widget.DrawerLayout.LOCK_MODE_UNLOCKED;

/**
 * Created by Zhuinden on 2017.07.26..
 */

public class MainActivity
        extends AppCompatActivity
        implements StateChanger {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private BackstackDelegate backstackDelegate;
    private FragmentStateChanger fragmentStateChanger;
    private ActionBarDrawerToggle drawerToggle;

    private final NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = (NavigationView.OnNavigationItemSelectedListener) item -> {
        switch(item.getItemId()) {
            case R.id.list_navigation_menu_item:
                backstackDelegate.getBackstack().goTo(TasksKey.create());
                break;
            case R.id.statistics_navigation_menu_item:
                backstackDelegate.getBackstack().goTo(StatisticsKey.create());
            default:
                break;
        }
        setCheckedItem(item.getItemId());
        // Close the navigation drawer when an item is selected.
        drawerLayout.closeDrawers();
        return true;
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        backstackDelegate = new BackstackDelegate();
        backstackDelegate.onCreate(savedInstanceState, getLastCustomNonConfigurationInstance(), History.single(TasksKey.create()));
        backstackDelegate.registerForLifecycleCallbacks(this);
        BackstackHolder backstackHolder = Injection.get().backstackHolder();
        backstackHolder.setBackstack(backstackDelegate.getBackstack()); // <-- make Backstack globally available through Dagger

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        setupDrawer();

        fragmentStateChanger = new FragmentStateChanger(this, getSupportFragmentManager(), R.id.contentFrame);
        backstackDelegate.setStateChanger(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return backstackDelegate.onRetainCustomNonConfigurationInstance();
    }

    @Override
    public void onBackPressed() {
        if(!backstackDelegate.getBackstack().goBack()) {
            super.onBackPressed();
        }
    }

    private void setupDrawer() {
        drawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
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
        // noinspection deprecation
        drawerLayout.setDrawerListener(drawerToggle);

        drawerToggle.setToolbarNavigationClickListener(v -> backstackDelegate.getBackstack().goBack());
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(true);
    }

    private void setCheckedItem(int navigationItemId) {
        Menu menu = navigationView.getMenu();
        for(int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            menuItem.setChecked(menuItem.getItemId() == navigationItemId);
        }
    }

    public void setupViewsForKey(BaseKey key) {
        if(key.shouldShowUp()) {
            drawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            drawerToggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            drawerLayout.setDrawerLockMode(LOCK_MODE_UNLOCKED, GravityCompat.START);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            drawerToggle.setDrawerIndicatorEnabled(true);
        }
        drawerToggle.syncState();
        setCheckedItem(key.navigationViewId());
        supportInvalidateOptionsMenu();
        if(key.isFabVisible()) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(key.getFragmentTag());
        key.setupFab(fragment, fab);
    }

    @Override
    public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
        bindActiveViewModels(stateChange);
        if(!stateChange.topNewState().equals(stateChange.topPreviousState())) {
            fragmentStateChanger.handleStateChange(stateChange);
            setupViewsForKey(stateChange.topNewState());
            String title = stateChange.<BaseKey>topNewState().title(getResources());
            setTitle(title == null ? getString(R.string.app_name) : title);
        }
        destroyInactiveViewModels(stateChange);
        completionCallback.stateChangeComplete();
    }

    private void bindActiveViewModels(@NonNull StateChange stateChange) {
        for(BaseKey<?> newKey : stateChange.<BaseKey<?>>getNewState()) {
            ViewModelLifecycleHelper.bindViewModel(this, newKey.getViewModelCreator(), newKey.getViewModelTag());
        }
    }

    private void destroyInactiveViewModels(StateChange stateChange) {
        List<BaseKey<?>> newKeys = stateChange.getNewState();
        for(BaseKey<?> previousKey : stateChange.<BaseKey<?>>getPreviousState()) {
            if(!newKeys.contains(previousKey)) {
                ViewModelLifecycleHelper.destroyViewModel(this, previousKey.getViewModelTag());
            }
        }
    }
}
