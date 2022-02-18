package io.github.wand555.richerconversation;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationPrefix;

/**
 * A ConversationPrefix implementation prepends all output from the conversation to the player.
 * The ConversationPrefix can be used to display the plugin name or conversation status as the conversation evolves.
 * Supports {@link BaseComponent}.
 */
public interface RicherPrefix extends ConversationPrefix {

    /**
     * Gets the prefix to use before each message to the player.
     * @param context Context information about the conversation.
     * @return The prefix text as a {@link BaseComponent}.
     */
    public BaseComponent getRicherPrefix(ConversationContext context);

    @Override
    default String getPrefix(ConversationContext context) {
        return getRicherPrefix(context).toPlainText();
    }
}
