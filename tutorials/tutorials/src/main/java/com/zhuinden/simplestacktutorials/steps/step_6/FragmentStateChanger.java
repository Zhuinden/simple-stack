package com.zhuinden.simplestacktutorials.steps.step_6;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestacktutorials.R;

import java.util.List;

public class FragmentStateChanger {
    private FragmentManager fragmentManager;
    private int containerId;

    public FragmentStateChanger(FragmentManager fragmentManager, int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    public void handleStateChange(StateChange stateChange) {
        fragmentManager.executePendingTransactions();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().disallowAddToBackStack();
        if (stateChange.getDirection() == StateChange.FORWARD) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        } else if (stateChange.getDirection() == StateChange.BACKWARD) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right, R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        } else { // REPLACE
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        }

        List<Step6Screen> previousKeys = stateChange.getPreviousKeys();
        List<Step6Screen> newKeys = stateChange.getNewKeys();
        for (Step6Screen oldKey : previousKeys) {
            Fragment fragment = fragmentManager.findFragmentByTag(oldKey.getFragmentTag());
            if (fragment != null) {
                if (!newKeys.contains(oldKey)) {
                    fragmentTransaction.remove(fragment); // remove fragments not in backstack
                } else if (!fragment.isDetached()) {
                    fragmentTransaction.detach(fragment); // destroy view of fragment not top
                }
            }
        }
        for (Step6Screen newKey : newKeys) {
            Fragment fragment = fragmentManager.findFragmentByTag(newKey.getFragmentTag());
            if (newKey.equals(stateChange.topNewKey())) {
                if (fragment != null) {
                    if (fragment.isRemoving()) { // fragments are quirky, they die asynchronously. Ignore if they're still there.
                        fragmentTransaction.replace(containerId, newKey.createFragment(), newKey.getFragmentTag());
                    } else if (fragment.isDetached()) {
                        fragmentTransaction.attach(fragment); // show top fragment if already exists
                    }
                } else {
                    fragment = newKey.createFragment(); // create and add new top if did not exist
                    fragmentTransaction.add(containerId, fragment, newKey.getFragmentTag());
                }
            } else {
                if (fragment != null && !fragment.isDetached()) {
                    fragmentTransaction.detach(fragment); // fragment not on top should not be showing
                }
            }
        }
        fragmentTransaction.commitAllowingStateLoss();
    }
}
