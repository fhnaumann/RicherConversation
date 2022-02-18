package io.github.wand555.richerconversationexample.prompts;

import io.github.wand555.richerconversation.prompts.RicherPrompt;
import io.github.wand555.richerconversationexample.ExamplePlugin;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DayPrompt extends StringPrompt implements RicherPrompt {
    @Override
    public BaseComponent getRicherPromptText(ConversationContext context) {
        return new TextComponent(new ComponentBuilder("So how's your day been?").color(ExamplePlugin.QUESTION_COLOR).create());
    }

    @Nullable
    @Override
    public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        return END_OF_CONVERSATION;
    }
}
