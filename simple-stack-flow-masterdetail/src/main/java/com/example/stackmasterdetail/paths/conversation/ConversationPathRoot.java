package com.example.stackmasterdetail.paths.conversation;

import com.example.stackmasterdetail.paths.MasterDetailPath;
import com.example.stackmasterdetail.paths.conversation.conversationlist.ConversationListPath;

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
