package me.wand555.github.io.betterconversation;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;

public class AgePrompt extends NumericPrompt {
    @Override
    protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
        return new AwesomePrompt();
    }

    @Override
    protected boolean isInputValid(ConversationContext context, String input) {

        System.out.println("testing valid");
        return super.isInputValid(context, input);
    }

    @Override
    public String getPromptText(ConversationContext context) {
        return "Gib dein Alter an";
    }
}
