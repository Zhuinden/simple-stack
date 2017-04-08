package com.example.stackmasterdetailfrag.paths.conversation.conversation;

import com.example.stackmasterdetailfrag.R;
import com.example.stackmasterdetailfrag.paths.conversation.ConversationPathRoot;
import com.google.auto.value.AutoValue;

/**
 * Created by Zhuinden on 2017.04.08..
 */


@AutoValue
public abstract class ConversationPath
        extends ConversationPathRoot {
    public static ConversationPath create(int conversationIndex) {
        return new AutoValue_ConversationPath(conversationIndex);
    }

    @Override
    public String getTitle() {
        return "Conversation";
    }

    @Override
    public int layout() {
        return R.layout.conversation_view;
    }
}