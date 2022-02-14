package me.wand555.github.io.betterconversation;

import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

public class TestStartPrompt extends BooleanPrompt {
    @Override
    protected Prompt acceptValidatedInput(ConversationContext context, boolean input) {
        return new AgePrompt();
    }

    @Override
    public String getPromptText(ConversationContext context) {
        return "Schreib ein Boolean!";
    }
}
