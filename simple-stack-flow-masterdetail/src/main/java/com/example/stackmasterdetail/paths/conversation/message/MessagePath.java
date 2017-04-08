package com.example.stackmasterdetail.paths.conversation.message;

import com.example.stackmasterdetail.R;
import com.example.stackmasterdetail.paths.conversation.ConversationPathRoot;
import com.google.auto.value.AutoValue;

/**
 * Created by Zhuinden on 2017.04.08..
 */


@AutoValue
public abstract class MessagePath
        extends ConversationPathRoot {
    public abstract int messageId();

    public static MessagePath create(int messageIndex, int position) {
        return new AutoValue_MessagePath(messageIndex, position);
    }

    @Override
    public String getTitle() {
        return "Message";
    }

    @Override
    public int layout() {
        return R.layout.message_view;
    }
}