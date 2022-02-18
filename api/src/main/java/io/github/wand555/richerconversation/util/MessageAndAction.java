package io.github.wand555.richerconversation.util;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

import java.util.Deque;

public record MessageAndAction(BaseComponent message, TriConsumer<ConversationContext, Deque<PromptAndAnswer>, Prompt> action) {
}
