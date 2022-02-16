package io.github.wand555.richerconversation;

import io.github.wand555.richerconversation.util.PromptAndAnswer;
import org.bukkit.conversations.ConversationContext;

import java.util.function.BiFunction;

@FunctionalInterface
public interface Formatter<R> extends BiFunction<PromptAndAnswer, ConversationContext, R> {
}
