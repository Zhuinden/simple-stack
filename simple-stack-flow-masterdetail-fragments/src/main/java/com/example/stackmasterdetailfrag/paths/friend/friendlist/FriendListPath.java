package com.example.stackmasterdetailfrag.paths.friend.friendlist;

import com.example.stackmasterdetailfrag.R;
import com.example.stackmasterdetailfrag.paths.friend.FriendPathRoot;
import com.google.auto.value.AutoValue;

/**
 * Created by Zhuinden on 2017.04.08..
 */


@AutoValue
public abstract class FriendListPath
        extends FriendPathRoot {
    @Override
    public int index() {
        return -1;
    }

    public static FriendListPath create() {
        return new AutoValue_FriendListPath();
    }

    @Override
    public String getTitle() {
        return "Friend List";
    }

    @Override
    public int layout() {
        return R.layout.friend_list_view;
    }
}