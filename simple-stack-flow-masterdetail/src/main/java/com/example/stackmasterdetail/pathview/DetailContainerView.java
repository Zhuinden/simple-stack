package com.example.stackmasterdetail.pathview;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.example.stackmasterdetail.Paths;
import com.zhuinden.simplestack.StateChanger;

import static com.example.stackmasterdetail.Paths.MasterDetailPath;

public class DetailContainerView
        extends FramePathContainerView {
    public DetailContainerView(Context context) {
        super(context);
    }

    public DetailContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DetailContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DetailContainerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        protected int getLayout(Parcelable path) {
            MasterDetailPath mdPath = (MasterDetailPath) path;
            return super.getLayout(mdPath.isMaster() ? Paths.NoDetails.create() : mdPath);
        }
    }
}
