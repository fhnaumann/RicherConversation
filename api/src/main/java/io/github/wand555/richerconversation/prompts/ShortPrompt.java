package io.github.wand555.richerconversation.prompts;

import io.github.wand555.richerconversation.util.QuestionAndPrompt;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ShortPrompt extends Prompt {

    QuestionAndPrompt<String> getShort(ConversationContext context);

    @NotNull
    @Override
    default String getPromptText(@NotNull ConversationContext context) {
        return getShort(context).t();
    }

    @Override
    default boolean blocksForInput(@NotNull ConversationContext context) {
        return true;
    }

    @Nullable
    @Override
    default Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        return getShort(context).prompt();
    }
}
