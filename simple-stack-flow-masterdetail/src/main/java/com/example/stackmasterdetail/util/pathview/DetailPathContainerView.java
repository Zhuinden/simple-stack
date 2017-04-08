package com.example.stackmasterdetail.util.pathview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.example.stackmasterdetail.application.Path;
import com.example.stackmasterdetail.paths.MasterDetailPath;
import com.example.stackmasterdetail.paths.NoDetailsPath;
import com.zhuinden.simplestack.StateChanger;

public class DetailPathContainerView
        extends FramePathContainerView {
    public DetailPathContainerView(Context context) {
        super(context);
    }

    public DetailPathContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DetailPathContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DetailPathContainerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public StateChanger createContainer() {
        return new DetailPathContainer(this);
    }

    static class DetailPathContainer
            extends SimpleStateChanger {
        public DetailPathContainer(ViewGroup root) {
            super(root);
        }

        @Override
        protected Path getActiveKey(Path path) {
            MasterDetailPath mdPath = (MasterDetailPath) path;
            return mdPath.isMaster() ? NoDetailsPath.create() : mdPath;
        }
    }
}
