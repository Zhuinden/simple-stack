package com.example.fragmenttransitions.core.navigation;

import android.os.Build;
import androidx.fragment.app.BackstackRecordAccessor;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.transition.Fade;

import com.example.fragmenttransitions.core.sharedelements.DetailsTransition;
import com.example.fragmenttransitions.core.sharedelements.HasSharedElement;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Zhuinden on 2020. 12. 18..
 */

public class SharedElementFragmentStateChanger extends DefaultFragmentStateChanger {
    public SharedElementFragmentStateChanger(FragmentManager fragmentManager, int containerId) {
        super(fragmentManager, containerId);
    }

    @Override
    protected void onBackwardNavigation(@Nonnull FragmentTransaction fragmentTransaction, @Nonnull StateChange stateChange) {
        // prevent default animation
    }

    @Override
    protected void onForwardNavigation(@Nonnull FragmentTransaction fragmentTransaction, @Nonnull StateChange stateChange) {
        // prevent default animation
    }

    @Override
    protected void onReplaceNavigation(@Nonnull FragmentTransaction fragmentTransaction, @Nonnull StateChange stateChange) {
        // prevent default animation
    }

    @Override
    protected void startShowing(@Nonnull FragmentTransaction fragmentTransaction, @Nonnull Fragment fragment) {
        fragmentTransaction.show(fragment);
    }

    @Override
    protected void stopShowing(@Nonnull FragmentTransaction fragmentTransaction, @Nonnull Fragment fragment) {
        fragmentTransaction.hide(fragment);
    }

    @Override
    protected boolean isNotShowing(@Nonnull Fragment fragment) {
        return fragment.isHidden();
    }

    @Override
    protected void configureFragmentTransaction(@Nonnull FragmentTransaction fragmentTransaction, @Nullable Fragment topPreviousFragment, @Nonnull Fragment topNewFragment, @Nonnull StateChange stateChange) {
        BaseKey topNewKey = stateChange.topNewKey();
        BaseKey topPreviousKey = stateChange.topPreviousKey();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(topPreviousFragment != null) {
                topPreviousFragment.setExitTransition(new Fade());
            }
            topNewFragment.setEnterTransition(new Fade());

            if(topNewKey instanceof HasSharedElement || topPreviousKey instanceof HasSharedElement) {
                topNewFragment.setSharedElementEnterTransition(new DetailsTransition());
                topNewFragment.setSharedElementReturnTransition(new DetailsTransition());
                if(topPreviousFragment != null) {
                    topPreviousFragment.setSharedElementEnterTransition(new DetailsTransition());
                    topPreviousFragment.setSharedElementReturnTransition(new DetailsTransition());
                }
            }
        }

        fragmentTransaction.setReorderingAllowed(true);

        if(topNewKey instanceof HasSharedElement) {
            HasSharedElement forwardKey = (HasSharedElement) topNewKey;
            if(forwardKey.sharedElement() != null) {
                // to eliminate use of WeakReference<View>, we add the view's transition name to the FragmentTransaction directly.
                BackstackRecordAccessor.addSharedElement(fragmentTransaction, forwardKey.sharedElement().sourceTransitionName(), forwardKey.sharedElement().targetTransitionName());
            }
        }
        if(topPreviousKey instanceof HasSharedElement) {
            HasSharedElement backwardKey = (HasSharedElement) topPreviousKey;
            if(backwardKey.sharedElement() != null) {
                // during back navigation, we must invert the received "source" and "target".
                BackstackRecordAccessor.addSharedElement(fragmentTransaction, backwardKey.sharedElement().targetTransitionName(), backwardKey.sharedElement().sourceTransitionName());
            }
        }
    }
}
