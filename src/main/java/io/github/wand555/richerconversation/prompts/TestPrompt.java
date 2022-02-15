package io.github.wand555.richerconversation.prompts;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;

public class TestPrompt extends MessagePrompt implements RicherPrompt {

    @Override
    public BaseComponent getRicherPromptText(ConversationContext context) {
        return new TextComponent(new ComponentBuilder("Wow, I'm ").append("clickable").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "look at me")).create());
    }

    @Override
    public String getPromptText(ConversationContext context) {
        return "I'm not clickable...";
    }

    @Override
    protected Prompt getNextPrompt(ConversationContext context) {
        return null;
    }
}
