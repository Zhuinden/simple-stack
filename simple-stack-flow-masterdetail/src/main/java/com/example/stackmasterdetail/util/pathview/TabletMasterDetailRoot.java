package com.example.stackmasterdetail.util.pathview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.stackmasterdetail.R;
import com.example.stackmasterdetail.application.IsMasterView;
import com.example.stackmasterdetail.application.MainActivity;
import com.example.stackmasterdetail.paths.MasterDetailPath;
import com.example.stackmasterdetail.util.Container;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

/**
 * This view is shown only in landscape orientation on tablets. See
 * the explanation in {@link MainActivity#onCreate}.
 */
public class TabletMasterDetailRoot
        extends LinearLayout
        implements Container, StateChanger, HandlesBack {
    private FramePathContainerView masterContainer;
    private FramePathContainerView detailContainer;

    private boolean disabled;

    public TabletMasterDetailRoot(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return !disabled && super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        masterContainer = (FramePathContainerView) findViewById(R.id.master);
        detailContainer = (FramePathContainerView) findViewById(R.id.detail);
    }

    public ViewGroup getCurrentChild() {
        MasterDetailPath showing = Backstack.getKey(getContext());
        return showing.isMaster() ? masterContainer.getCurrentChild() : detailContainer.getCurrentChild();
    }

    @Override
    public ViewGroup getContainerView() {
        return this;
    }

    @Override
    public StateChanger createContainer() {
        return new SimpleStateChanger(this);
    }

    @Override
    public void handleStateChange(final StateChange stateChange, StateChanger.Callback callback) {

        class CountdownCallback
                implements StateChanger.Callback {
            final StateChanger.Callback wrapped;
            int countDown = 2;

            CountdownCallback(StateChanger.Callback wrapped) {
                this.wrapped = wrapped;
            }

            @Override
            public void stateChangeComplete() {
                countDown--;
                if(countDown == 0) {
                    disabled = false;
                    wrapped.stateChangeComplete();
                    ((IsMasterView) masterContainer.getCurrentChild()).updateSelection(stateChange.<MasterDetailPath>topNewState());
                }
            }
        }

        disabled = true;
        callback = new CountdownCallback(callback);
        detailContainer.handleStateChange(stateChange, callback);
        masterContainer.handleStateChange(stateChange, callback);
    }

    @Override
    public boolean onBackPressed() {
        return BackSupport.onBackPressed(detailContainer);
    }
}
