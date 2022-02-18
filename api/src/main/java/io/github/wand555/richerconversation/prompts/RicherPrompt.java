package io.github.wand555.richerconversation.prompts;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

/**
 * <p>
 *     A RicherPrompt allows the usage of {@link net.md_5.bungee.api.chat.BaseComponent} instead of plain strings only.
 *     If you intend to use BaseComponents in any child of {@link Prompt} you should let your class implement
 *     {@link RicherPrompt} as well.
 * </p>
 * <p>
 *     An example would be
 *     <pre>
 *     {@code
 *     public class MyCustomMessagePrompt extends MessagePrompt implements RicherPrompt {
 *         @Override
 *         public TextComponent getRicherPromptText(ConversationContext context) {
 *             return new TextComponent(new ComponentBuilder("Wow, I'm ")
 *                 .append("clickable")
 *                 .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "look at me"))
 *                 .create());
 *        }
 *    }
 * }
 *     </pre>
 * </p>
 * <p>
 *     Overriding {@link RicherPrompt#getPromptText(ConversationContext)} has barely any effect aside from one point:
 *     The majority of conversations are with a {@link org.bukkit.entity.Player} and not with a {@link org.bukkit.command.ConsoleCommandSender}.
 *     In case it is actually a {@link org.bukkit.command.ConsoleCommandSender} {@link RicherPrompt#getPromptText(ConversationContext)} will be called.
 *     See the description there for what it does.
 * </p>
 */
public interface RicherPrompt extends Prompt {

    /**
     * Gets the text as a TextComponent to display to the user when this prompt is first presented.
     * @param context Context information about the conversation.
     * @return The
     */
    public BaseComponent getRicherPromptText(ConversationContext context);

    /**
     * Default implementation for getting the prompt text.
     * Transforms the {@link BaseComponent} returned from {@link RicherPrompt#getRicherPromptText(ConversationContext)} into plain text.
     * Implementing classes may override it, but it is probably not useful.
     * Only if the {@link org.bukkit.conversations.Conversable} involved in this conversation is an instance of {@link org.bukkit.command.ConsoleCommandSender},
     * will the result of this method be used.
     * @param context Context information about the conversation.
     * @return What {@link RicherPrompt#getRicherPromptText(ConversationContext)} returns formatted to plain text.
     * @see #getRicherPromptText(ConversationContext)
     */
    @Override
    default String getPromptText(ConversationContext context) {
        return getRicherPromptText(context).toPlainText();
    }
}
