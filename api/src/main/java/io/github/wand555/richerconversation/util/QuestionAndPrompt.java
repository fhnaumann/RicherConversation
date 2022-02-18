package io.github.wand555.richerconversation.util;

import org.bukkit.conversations.Prompt;

/**
 * Record storing the question and prompt.
 * Makes the creation of {@link io.github.wand555.richerconversation.prompts.ShortPrompt}
 * and {@link io.github.wand555.richerconversation.prompts.RicherShortPrompt} possible.
 * @param <T> the type of the question, either {@link String} or {@link net.md_5.bungee.api.chat.BaseComponent} for now.
 */
public record QuestionAndPrompt<T>(T t, Prompt prompt) {
}
