package com.example.stackmasterdetailfrag.util.pathview;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.example.stackmasterdetailfrag.R;
import com.example.stackmasterdetailfrag.SinglePaneFragmentStateChanger;
import com.example.stackmasterdetailfrag.application.IsMasterView;
import com.example.stackmasterdetailfrag.paths.MasterDetailPath;
import com.example.stackmasterdetailfrag.util.FragmentManagerService;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

/**
 * Created by Zhuinden on 2017.02.12..
 */

public class SinglePaneRoot extends FrameLayout implements HandlesBack, StateChanger {
    public SinglePaneRoot(Context context) {
        super(context);
        init(context);
    }

    public SinglePaneRoot(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SinglePaneRoot(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public SinglePaneRoot(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    SinglePaneFragmentStateChanger singlePaneFragmentStateChanger;

    private void init(Context context) {
        if(!isInEditMode()) {
            singlePaneFragmentStateChanger = new SinglePaneFragmentStateChanger(FragmentManagerService.get(context), R.id.fragment_container);
        }
    }

    @Override
    public boolean onBackPressed() {
        FragmentManager fragmentManager = FragmentManagerService.get(getContext());
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if(fragment != null && fragment.getView() != null) {
            return BackSupport.onBackPressed(fragment.getView());
        }
        return false;
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        singlePaneFragmentStateChanger.handleStateChange(stateChange);
        FragmentManager fragmentManager = FragmentManagerService.get(getContext());
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if(fragment != null && fragment.getView() != null && fragment.getView() instanceof IsMasterView) {
            ((IsMasterView) fragment.getView()).updateSelection(stateChange.<MasterDetailPath>topNewState());
        }
        completionCallback.stateChangeComplete();
    }
}
