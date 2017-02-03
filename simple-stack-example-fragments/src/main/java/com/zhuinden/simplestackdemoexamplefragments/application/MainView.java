package com.zhuinden.simplestackdemoexamplefragments.application;

import android.content.Context;
import android.content.res.Configuration;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackdemoexamplefragments.R;
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.statistics.StatisticsKey;
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.tasks.TasksKey;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Owner on 2017. 01. 26..
 */

public class MainView
        extends DrawerLayout {
    @BindView(R.id.root)
    RelativeLayout root;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    DrawerLayout drawerLayout;

    @BindView(R.id.fab_add_task)
    FloatingActionButton fabAddTask;

    @Inject
    Backstack backstack;

    private void setCheckedItem(int navigationItemId) {
        Menu menu = navigationView.getMenu();
        for(int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            menuItem.setChecked(menuItem.getItemId() == navigationItemId);
        }
    }

    private ActionBarDrawerToggle drawerToggle;

    public void setupViewsForKey(Key key) {
        if(key.shouldShowUp()) {
            setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            drawerToggle.setDrawerIndicatorEnabled(false);
            MainActivity.get(getContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            setDrawerLockMode(LOCK_MODE_UNLOCKED, GravityCompat.START);
            MainActivity.get(getContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            drawerToggle.setDrawerIndicatorEnabled(true);
        }
        drawerToggle.syncState();
        setCheckedItem(key.navigationViewId());
        MainActivity.get(getContext()).supportInvalidateOptionsMenu();
        if(key.isFabVisible()) {
            fabAddTask.setVisibility(View.VISIBLE);
        } else {
            fabAddTask.setVisibility(View.GONE);
        }
        Fragment fragment = MainActivity.get(getContext()).getSupportFragmentManager().findFragmentByTag(key.getFragmentTag());
        fabAddTask.setOnClickListener(key.fabClickListener(fragment));
        if(key.fabDrawableIcon() != 0) {
            fabAddTask.setImageResource(key.fabDrawableIcon());
        }
    }

    private final NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = (NavigationView.OnNavigationItemSelectedListener) item -> {
        switch(item.getItemId()) {
            case R.id.list_navigation_menu_item:
                backstack.goTo(TasksKey.create());
                break;
            case R.id.statistics_navigation_menu_item:
                backstack.goTo(StatisticsKey.create());
            default:
                break;
        }
        setCheckedItem(item.getItemId());
        // Close the navigation drawer when an item is selected.
        drawerLayout.closeDrawers();
        return true;
    };

    public MainView(Context context) {
        super(context);
        init(context);
    }

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MainView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        if(!isInEditMode()) {
            CustomApplication.get(context).getComponent().inject(this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        drawerLayout = this;
    }

    public void onCreate() {
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);

        MainActivity.get(getContext()).setSupportActionBar(toolbar);
        ActionBar actionBar = MainActivity.get(getContext()).getSupportActionBar();
        drawerToggle = new ActionBarDrawerToggle(MainActivity.get(getContext()), drawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                MainActivity.get(getContext()).supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                MainActivity.get(getContext()).supportInvalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        drawerToggle.setToolbarNavigationClickListener(v -> backstack.goBack());
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(true);
    }

    public void onPostCreate() {
        drawerToggle.syncState();
    }

    public void onConfigChanged(Configuration newConfig) {
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return true;
        }
        return false;
    }
}
