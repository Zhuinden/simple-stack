package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.taskdetail;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.squareup.coordinators.Coordinators;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.application.MainActivity;

/**
 * Created by Zhuinden on 2017.01.26..
 */

public class TaskDetailView
        extends RelativeLayout
        implements MainActivity.OptionsItemSelectedListener {
    public TaskDetailView(Context context) {
        super(context);
    }

    public TaskDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TaskDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public TaskDetailView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.menu_delete:
                TaskDetailCoordinator taskDetailCoordinator = Coordinators.getCoordinator(this);
                taskDetailCoordinator.deleteTask();
                return true;
        }
        return false;
    }
}
