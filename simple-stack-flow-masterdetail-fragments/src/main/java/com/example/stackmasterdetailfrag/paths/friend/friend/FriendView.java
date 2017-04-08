/*
 * Copyright 2013 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.stackmasterdetailfrag.paths.friend.friend;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.stackmasterdetailfrag.R;
import com.example.stackmasterdetailfrag.data.model.User;
import com.example.stackmasterdetailfrag.util.Utils;
import com.zhuinden.simplestack.Backstack;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendView
        extends LinearLayout {
    @Inject
    List<User> friends;

    private final User friend;

    @BindView(R.id.friend_info)
    TextView friendInfo;

    public FriendView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Utils.getComponent(context).inject(this);

        FriendPath screen = Backstack.getKey(context);
        friend = friends.get(screen.index());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        friendInfo.setText("Name: " + friend.name);
    }
}
