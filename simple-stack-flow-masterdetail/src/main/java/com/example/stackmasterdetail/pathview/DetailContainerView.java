package com.example.stackmasterdetail.pathview;

import android.content.Context;
import android.util.AttributeSet;
import com.example.stackmasterdetail.Paths;
import com.example.stackmasterdetail.R;
import flow.path.Path;
import flow.path.PathContextFactory;

import static com.example.stackmasterdetail.Paths.MasterDetailPath;

public class DetailContainerView extends FramePathContainerView {

  public DetailContainerView(Context context, AttributeSet attrs) {
    super(context, attrs, new DetailPathContainer(R.id.screen_switcher_tag, Path.contextFactory()));
  }

  static class DetailPathContainer extends SimplePathContainer {
    DetailPathContainer(int tagKey, PathContextFactory contextFactory) {
      super(tagKey, contextFactory);
    }

    @Override protected int getLayout(Path path) {
      MasterDetailPath mdPath = (MasterDetailPath) path;
      return super.getLayout(mdPath.isMaster() ? new Paths.NoDetails() : mdPath);
    }
  }
}
