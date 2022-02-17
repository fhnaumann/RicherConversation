package io.github.wand555.richerconversation;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationPrefix;

public interface RicherPrefix extends ConversationPrefix {

    public BaseComponent getRicherPrefix(ConversationContext context);

    @Override
    default String getPrefix(ConversationContext context) {
        return getRicherPrefix(context).toPlainText();
    }
}
