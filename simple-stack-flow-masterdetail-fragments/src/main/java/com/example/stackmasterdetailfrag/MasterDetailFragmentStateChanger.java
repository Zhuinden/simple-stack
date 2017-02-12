package com.example.stackmasterdetailfrag;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.zhuinden.simplestack.StateChange;

/**
 * Created by Zhuinden on 2017.02.12..
 */

public class MasterDetailFragmentStateChanger {
    private final FragmentManager fragmentManager;
    private final int masterContainerId;
    private final int detailContainerId;

    public MasterDetailFragmentStateChanger(FragmentManager fragmentManager, int masterContainerId, int detailContainerId) {
        this.fragmentManager = fragmentManager;
        this.masterContainerId = masterContainerId;
        this.detailContainerId = detailContainerId;
    }
    
    public void handleStateChange(StateChange stateChange) {
        FragmentTransaction fragmentTransaction = beginFragmentTransaction(stateChange);
        
        for(Parcelable _previousKey : stateChange.getPreviousState()) {
            Paths.Path previousKey = (Paths.Path)_previousKey;
            if(!stateChange.getNewState().contains(_previousKey)) {
                Fragment previousFragment = fragmentManager.findFragmentByTag(previousKey.getFragmentTag());
                if(previousFragment != null) {
                    fragmentTransaction.remove(previousFragment);
                }
            }
        }

        Paths.MasterDetailPath topKey = stateChange.topNewState();
        
        Paths.Path masterKey;
        Paths.Path detailKey = Paths.NoDetails.create();
        if(!topKey.isMaster()) {
            Fragment noDetailsFragment = fragmentManager.findFragmentByTag(detailKey.getFragmentTag());
            if(noDetailsFragment != null) {
                fragmentTransaction.remove(noDetailsFragment);
            }
            detailKey = topKey;
            masterKey = topKey.getMaster();
        } else {
            masterKey = topKey;
        }
        
        for(Parcelable _newKey : stateChange.getNewState()) {
            Paths.Path newKey = (Paths.Path)_newKey;
            Fragment fragment = fragmentManager.findFragmentByTag(newKey.getFragmentTag()); 
            if(!newKey.equals(masterKey) && !newKey.equals(detailKey)) {
                if(fragment != null) {
                    fragmentTransaction.detach(fragment);
                }
            }
        }

        fragmentTransaction.commitNow();

        reattachFragmentForKey(stateChange, masterKey, masterContainerId);
        reattachFragmentForKey(stateChange, detailKey, detailContainerId);
    }

    private void reattachFragmentForKey(StateChange stateChange, Paths.Path key, int containerId) {
        FragmentTransaction fragmentTransaction;
        Fragment masterFragment = fragmentManager.findFragmentByTag(key.getFragmentTag());
        if(masterFragment == null) {
            fragmentTransaction = beginFragmentTransaction(stateChange);
            masterFragment = key.createFragment();
            fragmentTransaction.add(containerId, masterFragment, key.getFragmentTag());
            fragmentTransaction.commitNow();
        } else {
            if(masterFragment.isDetached() || masterFragment.getView() == null) {
                fragmentTransaction = beginFragmentTransaction(stateChange);
                Fragment.SavedState savedState = fragmentManager.saveFragmentInstanceState(masterFragment);
                fragmentTransaction.remove(masterFragment);
                fragmentTransaction.commitNow();
                fragmentTransaction = beginFragmentTransaction(stateChange);
                masterFragment.setInitialSavedState(savedState);
                fragmentTransaction.add(containerId, masterFragment, key.getFragmentTag());
                fragmentTransaction.commitNow();
                //fragmentTransaction.attach(masterFragment); // does not work with config change
            }
        }
    }

    @NonNull
    private FragmentTransaction beginFragmentTransaction(StateChange stateChange) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.disallowAddToBackStack();

        if(stateChange.getDirection() == StateChange.FORWARD) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        } else if(stateChange.getDirection() == StateChange.BACKWARD) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        }
        return fragmentTransaction;
    }
}