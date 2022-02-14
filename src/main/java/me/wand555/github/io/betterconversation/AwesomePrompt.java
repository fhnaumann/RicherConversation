package me.wand555.github.io.betterconversation;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;

public class AwesomePrompt extends MessagePrompt {
    @Override
    protected Prompt getNextPrompt(ConversationContext context) {
        return new NamePrompt();
    }

    @Override
    public String getPromptText(ConversationContext context) {
        return "You're awesome btw!";
    }
}
