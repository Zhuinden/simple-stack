package com.example.stackmasterdetailfrag.paths.friend;

import com.example.stackmasterdetailfrag.paths.MasterDetailPath;
import com.example.stackmasterdetailfrag.paths.friend.friendlist.FriendListPath;

/**
 * Created by Zhuinden on 2017.04.08..
 */

public abstract class FriendPathRoot
        extends MasterDetailPath {
    public abstract int index();

    @Override
    public MasterDetailPath getMaster() {
        return FriendListPath.create();
    }
}