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

package com.example.stackmasterdetailfrag.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.stackmasterdetailfrag.Paths;
import com.example.stackmasterdetailfrag.R;
import com.example.stackmasterdetailfrag.model.Conversation;
import com.example.stackmasterdetailfrag.model.User;
import com.example.stackmasterdetailfrag.util.BackstackService;
import com.example.stackmasterdetailfrag.util.Utils;
import com.zhuinden.simplestack.Backstack;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessageView
        extends LinearLayout {
    @Inject
    List<Conversation> conversations;

    @Inject
    List<User> friendList;

    private Conversation.Item message;

    @BindView(R.id.user)
    TextView userView;

    @BindView(R.id.message)
    TextView messageView;

    public MessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        Utils.getComponent(context).inject(this);

        Paths.Message screen = Backstack.getKey(context);
        message = conversations.get(screen.conversationIndex()).items.get(screen.messageId());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);

        userView.setText(String.valueOf(message.from));
        messageView.setText(String.valueOf(message.message));
    }

    @OnClick(R.id.user)
    void userClicked() {
        int position = friendList.indexOf(message.from);
        if(position != -1) {
            BackstackService.get(getContext()).goTo(Paths.Friend.create(position));
        }
    }
}