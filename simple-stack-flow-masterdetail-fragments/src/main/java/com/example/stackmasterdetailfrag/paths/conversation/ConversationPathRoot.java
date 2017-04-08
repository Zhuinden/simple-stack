package com.example.stackmasterdetailfrag.paths.conversation;

import com.example.stackmasterdetailfrag.paths.MasterDetailPath;
import com.example.stackmasterdetailfrag.paths.conversation.conversationlist.ConversationListPath;

/**
 * Created by Zhuinden on 2017.04.08..
 */

public abstract class ConversationPathRoot
        extends MasterDetailPath {
    public abstract int conversationIndex();

    @Override
    public MasterDetailPath getMaster() {
        return ConversationListPath.create();
    }
}
