package com.example.stackmasterdetailfrag.paths.conversation.conversationlist;

import com.example.stackmasterdetailfrag.R;
import com.example.stackmasterdetailfrag.paths.conversation.ConversationPathRoot;
import com.google.auto.value.AutoValue;

/**
 * Created by Zhuinden on 2017.04.08..
 */

@AutoValue
public abstract class ConversationListPath
        extends ConversationPathRoot {
    public ConversationListPath() {
    }

    @Override
    public int conversationIndex() {
        return -1;
    }

    public static ConversationListPath create() {
        return new AutoValue_ConversationListPath();
    }

    @Override
    public String getTitle() {
        return "Conversation List";
    }

    @Override
    public int layout() {
        return R.layout.conversation_list_view;
    }
}
