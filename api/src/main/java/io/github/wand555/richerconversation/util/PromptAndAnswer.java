package io.github.wand555.richerconversation.util;

import org.bukkit.conversations.Prompt;

/**
 * Record storing the prompt and the answer given by the user.
 * This is mainly used to keep track of the history during a conversation.
 */
public record PromptAndAnswer(Prompt prompt, String answer) {
}
