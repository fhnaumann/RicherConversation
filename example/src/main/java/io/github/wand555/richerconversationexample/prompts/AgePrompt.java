package io.github.wand555.richerconversationexample.prompts;

import io.github.wand555.richerconversation.prompts.RicherPrompt;
import io.github.wand555.richerconversationexample.ExamplePlugin;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AgePrompt extends NumericPrompt implements RicherPrompt {
    @Nullable
    @Override
    protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull Number input) {
        return new DayPrompt();
    }

    @Override
    public BaseComponent getRicherPromptText(ConversationContext context) {
        return new TextComponent(new ComponentBuilder("Alright. And how old are you?").color(ExamplePlugin.QUESTION_COLOR).create());
    }
}
