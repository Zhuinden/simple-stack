package com.zhuinden.simplestackexamplemvvm.application;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestack.navigator.Navigator;
import com.zhuinden.simplestackexamplemvvm.R;
import com.zhuinden.simplestackexamplemvvm.application.injection.Injector;
import com.zhuinden.simplestackexamplemvvm.features.statistics.StatisticsKey;
import com.zhuinden.simplestackexamplemvvm.features.tasks.TasksKey;
import com.zhuinden.simplestackexamplemvvm.util.ServiceProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

import static androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
import static androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED;

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

    @BindView(R.id.contentFrame)
    ViewGroup contentFrame;

    private FragmentStateChanger fragmentStateChanger;
    private ActionBarDrawerToggle drawerToggle;

    private final NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = (NavigationView.OnNavigationItemSelectedListener) item -> {
        switch(item.getItemId()) {
            case R.id.list_navigation_menu_item:
                Navigator.getBackstack(this).goTo(TasksKey.create());
                break;
            case R.id.statistics_navigation_menu_item:
                Navigator.getBackstack(this).goTo(StatisticsKey.create());
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
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        ButterKnife.bind(this);

        setupDrawer();

        fragmentStateChanger = new FragmentStateChanger(this, getSupportFragmentManager(), R.id.contentFrame);

        Backstack backstack = Navigator.configure()
                .setStateChanger(this)
                .setScopedServices(new ServiceProvider())
                .setDeferredInitialization(true)
                .install(this, contentFrame, History.of(TasksKey.create()));

        BackstackHolder backstackHolder = Injector.get().backstackHolder();
        backstackHolder.setBackstack(backstack); // <-- make Backstack globally available through Dagger, singleInstance only!

        Navigator.executeDeferredInitialization(this);
    }

    @Override
    public void onBackPressed() {
        if(!Navigator.onBackPressed(this)) {
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

        drawerToggle.setToolbarNavigationClickListener(v -> Navigator.onBackPressed(this));
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
            fab.show();
        } else {
            fab.hide();
        }
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(key.getFragmentTag());
        key.setupFab(fragment, fab);
    }

    @Override
    public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
        if(!stateChange.isTopNewKeyEqualToPrevious()) {
            fragmentStateChanger.handleStateChange(stateChange);
            setupViewsForKey(stateChange.topNewKey());
            String title = stateChange.<BaseKey>topNewKey().title(getResources());
            setTitle(title == null ? getString(R.string.app_name) : title);
        }
        completionCallback.stateChangeComplete();
    }
}
