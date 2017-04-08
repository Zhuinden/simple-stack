package com.example.stackmasterdetailfrag.paths;

import com.example.stackmasterdetailfrag.application.Path;

/**
 * Identifies screens in a master / detail relationship. Both master and detail screens
 * extend this class.
 * <p>
 * Not a lot of thought has been put into making a decent master / detail modeling here. Rather
 * this is an excuse to show off using Flow to build a responsive layout. See {@link
 * com.example.stackmasterdetailfrag.util.pathview.TabletMasterDetailRoot}.
 */
public abstract class MasterDetailPath
        extends Path {
    /**
     * Returns the screen that shows the master list for this type of screen.
     * If this screen is the master, returns self.
     * <p>
     * For example, the {@link com.example.stackmasterdetailfrag.paths.conversation.conversation.ConversationPath} and {@link com.example.stackmasterdetailfrag.paths.conversation.message.MessagePath} screens are both
     * "under" the master {@link com.example.stackmasterdetailfrag.paths.conversation.conversationlist.ConversationListPath} screen. All three of these
     * screens return a {@link com.example.stackmasterdetailfrag.paths.conversation.conversation.ConversationPath} from this method.
     */
    public abstract MasterDetailPath getMaster();

    public final boolean isMaster() {
        return equals(getMaster());
    }
}
