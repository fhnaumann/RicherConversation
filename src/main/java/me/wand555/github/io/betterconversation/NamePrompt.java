package me.wand555.github.io.betterconversation;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class NamePrompt extends StringPrompt {
    @Override
    public String getPromptText(ConversationContext context) {
        return "Schreibe deinen Namen";
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        return END_OF_CONVERSATION;
    }
}
