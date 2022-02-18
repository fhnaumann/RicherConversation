package io.github.wand555.richerconversation;

import io.github.wand555.richerconversation.util.PromptAndAnswer;
import org.bukkit.conversations.ConversationContext;

import java.util.function.BiFunction;

/**
 * Base interface defining the input variable types in the BiFunction.
 * @param <R> The type of formatting.
 */
@FunctionalInterface
public interface Formatter<R> extends BiFunction<PromptAndAnswer, ConversationContext, R> {
}
