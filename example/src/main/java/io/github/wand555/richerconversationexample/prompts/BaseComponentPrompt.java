package io.github.wand555.richerconversationexample.prompts;

import io.github.wand555.richerconversation.prompts.RicherPrompt;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;

public class BaseComponentPrompt extends MessagePrompt implements RicherPrompt {

    @Override
    public BaseComponent getRicherPromptText(ConversationContext context) {
        return new TextComponent(new ComponentBuilder()
                .append("I'm a ")
                .color(ChatColor.of("#C38D9E"))
                .append("message created from")
                .append(" base components ")
                .color(ChatColor.of("#3FEEE6"))
                .bold(true)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(new ComponentBuilder()
                        .append("Yes, really!")
                        .color(ChatColor.of("#E8A87C"))
                        .create())))
                .bold(false)
                .append("in a conversation prompt")
                .underlined(true)
                .append(".")
                .underlined(false)
                .color(ChatColor.of("#C38D9E"))
                .create());
    }

    @Override
    protected Prompt getNextPrompt(ConversationContext context) {
        return END_OF_CONVERSATION;
    }
}
