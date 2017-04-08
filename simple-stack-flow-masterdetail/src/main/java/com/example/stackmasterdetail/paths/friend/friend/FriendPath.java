package com.example.stackmasterdetail.paths.friend.friend;

import com.example.stackmasterdetail.R;
import com.example.stackmasterdetail.paths.friend.FriendPathRoot;
import com.google.auto.value.AutoValue;

/**
 * Created by Zhuinden on 2017.04.08..
 */

@AutoValue
public abstract class FriendPath
        extends FriendPathRoot {
    public static FriendPath create(int position) {
        return new AutoValue_FriendPath(position);
    }

    @Override
    public String getTitle() {
        return "Friend";
    }

    @Override
    public int layout() {
        return R.layout.friend_view;
    }
}