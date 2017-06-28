package com.example.stackmasterdetail.util.pathview;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.example.stackmasterdetail.application.Path;
import com.example.stackmasterdetail.paths.MasterDetailPath;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

public class MasterPathContainerView
        extends FramePathContainerView {
    public MasterPathContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void handleStateChange(@NonNull StateChange stateChange, @NonNull final StateChanger.Callback callback) {
        Parcelable previousKey = stateChange.topPreviousState();
        MasterDetailPath currentMaster = previousKey != null ? ((MasterDetailPath) previousKey).getMaster() : null;

        MasterDetailPath newMaster = ((MasterDetailPath) stateChange.topNewState()).getMaster();

        // Short circuit if the new screen has the same master.
        if(getCurrentChild() != null && newMaster.equals(currentMaster)) {
            callback.stateChangeComplete();
        } else {
            super.handleStateChange(stateChange, new StateChanger.Callback() {
                @Override
                public void stateChangeComplete() {
                    callback.stateChangeComplete();
                }
            });
        }
    }

    @Override
    public StateChanger createContainer() {
        return new MasterPathContainer(this);
    }

    static class MasterPathContainer
            extends SimpleStateChanger {
        public MasterPathContainer(ViewGroup root) {
            super(root);
        }

        @Override
        protected Path getActiveKey(Path path) {
            MasterDetailPath mdPath = (MasterDetailPath) path;
            return mdPath.getMaster();
        }
    }
}
