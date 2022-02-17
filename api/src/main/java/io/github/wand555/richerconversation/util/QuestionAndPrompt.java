package io.github.wand555.richerconversation.util;

import org.bukkit.conversations.Prompt;

public record QuestionAndPrompt<T>(T t, Prompt prompt) {
}
