package com.example.stackmasterdetailfrag.util.pathview;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.example.stackmasterdetailfrag.MasterDetailFragmentStateChanger;
import com.example.stackmasterdetailfrag.R;
import com.example.stackmasterdetailfrag.application.IsMasterView;
import com.example.stackmasterdetailfrag.application.MainActivity;
import com.example.stackmasterdetailfrag.paths.MasterDetailPath;
import com.example.stackmasterdetailfrag.util.FragmentManagerService;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

/**
 * This view is shown only in landscape orientation on tablets. See
 * the explanation in {@link MainActivity#onCreate}.
 */
public class TabletMasterDetailRoot
        extends LinearLayout
        implements StateChanger, HandlesBack {
    public TabletMasterDetailRoot(Context context) {
        super(context);
        init(context);
    }

    public TabletMasterDetailRoot(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TabletMasterDetailRoot(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public TabletMasterDetailRoot(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    MasterDetailFragmentStateChanger masterDetailFragmentStateChanger;

    private void init(Context context) {
        if(!isInEditMode()) {
            masterDetailFragmentStateChanger = new MasterDetailFragmentStateChanger(FragmentManagerService.get(context), R.id.master, R.id.detail);
        }
    }

    @Override
    public void handleStateChange(final StateChange stateChange, StateChanger.Callback callback) {
        masterDetailFragmentStateChanger.handleStateChange(stateChange);
        FragmentManager fragmentManager = FragmentManagerService.get(getContext());
        Fragment fragment = fragmentManager.findFragmentById(R.id.master);
        if(fragment != null && fragment.getView() != null && fragment.getView() instanceof IsMasterView) {
            ((IsMasterView) fragment.getView()).updateSelection(stateChange.<MasterDetailPath>topNewState());
        }
        callback.stateChangeComplete();
    }

    @Override
    public boolean onBackPressed() {
        FragmentManager fragmentManager = FragmentManagerService.get(getContext());
        Fragment fragment = fragmentManager.findFragmentById(R.id.detail);
        if(fragment != null && fragment.getView() != null) {
            return BackSupport.onBackPressed(fragment.getView());
        }
        return false;
    }
}
