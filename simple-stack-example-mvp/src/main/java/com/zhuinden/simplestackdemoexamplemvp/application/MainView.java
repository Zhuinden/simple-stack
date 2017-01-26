package com.zhuinden.simplestackdemoexamplemvp.application;

import android.content.Context;
import android.content.res.Configuration;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.statistics.StatisticsKey;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks.TasksKey;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Owner on 2017. 01. 26..
 */

public class MainView
        extends DrawerLayout
        implements MainActivity.OptionsItemSelectedListener {
    @BindView(R.id.root)
    RelativeLayout root;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    DrawerLayout drawerLayout;

    @BindView(R.id.fab_add_task)
    FloatingActionButton fabAddTask;

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
            drawerToggle.setDrawerIndicatorEnabled(false);
            MainActivity.get(getContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
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
    }


    private final NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = (NavigationView.OnNavigationItemSelectedListener) item -> {
        switch(item.getItemId()) {
            case R.id.list_navigation_menu_item:
                Backstack.get(getContext()).goTo(TasksKey.create());
                break;
            case R.id.statistics_navigation_menu_item:
                Backstack.get(getContext()).goTo(StatisticsKey.create());
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
    }

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        drawerLayout = this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(root != null && root.getChildAt(0) != null) {
            boolean handled = false;
            if(root.getChildAt(0) instanceof MainActivity.OptionsItemSelectedListener) {
                handled = ((MainActivity.OptionsItemSelectedListener) (root.getChildAt(0))).onOptionsItemSelected(item);
            }
            if(handled) {
                return handled;
            }
        }
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if(root != null && root.getChildAt(0) != null) {
            Key key = Backstack.getKey(root.getChildAt(0).getContext());
            MainActivity.get(getContext()).getMenuInflater().inflate(key.menu(), menu);
            return true;
        }
        return false;
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

        drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Backstack.get(getContext()).goBack();
            }
        });
        //actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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
