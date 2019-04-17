package android.support.v4.app;

import java.util.ArrayList;

/**
 * This class allows writing the source view's transition name, without having to pass the View itself to the FragmentManager.
 *
 * @author zhuinden
 */
public class BackstackRecordAccessor {
    private BackstackRecordAccessor() {
    }

    /**
     * To skip passing the View itself (which would be stored as transient WeakReference), we can instead write the source name into the FragmentTransaction directly.
     *
     * @param fragmentTransaction  the target fragment transaction
     * @param sourceTransitionName the source transition name
     * @param targetTransitionName the target transition name
     * @return the fragment transaction
     */
    public static FragmentTransaction addSharedElement(FragmentTransaction fragmentTransaction, String sourceTransitionName, String targetTransitionName) {
        if(FragmentTransition.supportsTransition() && fragmentTransaction instanceof BackStackRecord) {
            if(sourceTransitionName == null) {
                throw new IllegalArgumentException("Unique transitionNames are required for all" +
                        " sharedElements");
            }
            BackStackRecord backStackRecord = (BackStackRecord) fragmentTransaction;
            if(backStackRecord.mSharedElementSourceNames == null) {
                backStackRecord.mSharedElementSourceNames = new ArrayList<>();
                backStackRecord.mSharedElementTargetNames = new ArrayList<>();
            } else if(backStackRecord.mSharedElementTargetNames.contains(targetTransitionName)) {
                throw new IllegalArgumentException("A shared element with the target name '"
                        + targetTransitionName + "' has already been added to the transaction.");
            } else if(backStackRecord.mSharedElementSourceNames.contains(sourceTransitionName)) {
                throw new IllegalArgumentException("A shared element with the source name '"
                        + sourceTransitionName + " has already been added to the transaction.");
            }

            backStackRecord.mSharedElementSourceNames.add(sourceTransitionName);
            backStackRecord.mSharedElementTargetNames.add(targetTransitionName);
        }
        return fragmentTransaction;
    }
}
