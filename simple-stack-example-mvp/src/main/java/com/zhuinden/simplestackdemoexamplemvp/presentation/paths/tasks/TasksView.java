package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuItem;

import com.squareup.coordinators.Coordinators;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.application.MainActivity;
import com.zhuinden.simplestackdemoexamplemvp.util.ScrollChildSwipeRefreshLayout;

/**
 * Created by Owner on 2017. 01. 26..
 */

public class TasksView
        extends ScrollChildSwipeRefreshLayout
        implements MainActivity.OptionsItemSelectedListener, StateChanger {
    public TasksView(Context context) {
        super(context);
    }

    public TasksView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        TasksCoordinator tasksCoordinator = Coordinators.getCoordinator(this);
        switch(menuItem.getItemId()) {
            case R.id.menu_filter:
                tasksCoordinator.showFilteringPopupMenu();
                return true;
            case R.id.menu_clear:
                tasksCoordinator.clear();
                return true;
            case R.id.menu_refresh:
                tasksCoordinator.refresh();
                return true;
            default:
        }
        return false;
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        // hack fix from  http://stackoverflow.com/a/27073879/2413303 to fix view staying on screen
        setRefreshing(false);
        destroyDrawingCache();
        clearAnimation();
        // end
        completionCallback.stateChangeComplete();
    }
}
