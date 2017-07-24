package com.example.stackmasterdetailfrag;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.stackmasterdetailfrag.application.Path;
import com.example.stackmasterdetailfrag.paths.MasterDetailPath;
import com.example.stackmasterdetailfrag.paths.NoDetailsPath;
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
        MasterDetailPath topKey = stateChange.topNewState();
        Path masterKey;
        Path detailKey = NoDetailsPath.create();
        if(!topKey.isMaster()) {
            detailKey = topKey;
            masterKey = topKey.getMaster();
        } else {
            masterKey = topKey;
        }
        Fragment masterFragment = removeToDetachFragment(stateChange, masterKey);
        Fragment detailFragment = removeToDetachFragment(stateChange, detailKey);

        FragmentTransaction fragmentTransaction = beginFragmentTransaction(stateChange);

        if(!topKey.isMaster()) {
            removeFragment(fragmentTransaction, NoDetailsPath.create());
        }

        for(Path previousKey : stateChange.<Path>getPreviousState()) {
            if(!stateChange.getNewState().contains(previousKey)) {
                removeFragment(fragmentTransaction, previousKey);
            } else {
                if(!previousKey.equals(masterKey) && !previousKey.equals(detailKey)) {
                    detachFragment(fragmentTransaction, previousKey);
                }
            }
        }

        Path previousTop = stateChange.topPreviousState(); // remove outlying master
        if(previousTop != null && (previousTop instanceof MasterDetailPath)) {
            MasterDetailPath previousMasterDetailTop = (MasterDetailPath) previousTop;
            if(!previousMasterDetailTop.isMaster() && !stateChange.getNewState()
                    .contains(previousMasterDetailTop) && !stateChange.getNewState()
                    .contains(previousMasterDetailTop.getMaster()) && !stateChange.<MasterDetailPath>topNewState().getMaster()
                    .equals(previousMasterDetailTop.getMaster())) {
                Fragment previousMaster = fragmentManager.findFragmentByTag(previousMasterDetailTop.getMaster().getFragmentTag());
                if(previousMaster != null) {
                    fragmentTransaction.remove(previousMaster);
                }
            }
        }

        for(Path newKey : stateChange.<Path>getNewState()) {
            if(!newKey.equals(masterKey) && !newKey.equals(detailKey)) {
                detachFragment(fragmentTransaction, newKey);
            }
        }

        reattachRemovedFragment(masterFragment, fragmentTransaction, masterKey, masterContainerId);
        reattachRemovedFragment(detailFragment, fragmentTransaction, detailKey, detailContainerId);

        fragmentTransaction.commitNow();
    }

    private void detachFragment(FragmentTransaction fragmentTransaction, Path key) {
        Fragment fragment = fragmentManager.findFragmentByTag(key.getFragmentTag());
        if(fragment != null) {
            fragmentTransaction.detach(fragment);
        }
    }

    private void removeFragment(FragmentTransaction fragmentTransaction, Path path) {
        Fragment fragment = fragmentManager.findFragmentByTag(path.getFragmentTag());
        if(fragment != null) {
            fragmentTransaction.remove(fragment);
        }
    }

    private void reattachRemovedFragment(Fragment fragment, FragmentTransaction fragmentTransaction, Path key, int containerId) {
        if(fragment == null) {
            fragment = key.createFragment();
        }
        if(!fragment.isAdded() || (fragment.isDetached() || fragment.getView() == null)) {
            fragmentTransaction.add(containerId, fragment, key.getFragmentTag());
        }
    }

    private Fragment removeToDetachFragment(StateChange stateChange, Path key) {
        Fragment fragment = fragmentManager.findFragmentByTag(key.getFragmentTag());
        if(fragment != null && (fragment.isDetached() || fragment.getView() == null)) {
            FragmentTransaction fragmentTransaction = beginFragmentTransaction(stateChange);
            Fragment.SavedState savedState = fragmentManager.saveFragmentInstanceState(fragment);
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commitNow();
            fragment.setInitialSavedState(savedState);
        }
        return fragment;
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