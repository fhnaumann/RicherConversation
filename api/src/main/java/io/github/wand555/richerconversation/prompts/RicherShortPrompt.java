package io.github.wand555.richerconversation.prompts;

import io.github.wand555.richerconversation.util.QuestionAndPrompt;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Creates a richer prompt. This interface combines the return values from {@link RicherPrompt#getRicherPromptText(ConversationContext)}
 * and {@link Prompt#acceptInput(ConversationContext, String)} into one type in order to be created
 * with lambda syntax.
 * It is useful when the prompt requires an input from the user which doesn't have to validated.
 * So
 * <pre>
 *     {@code
 *     Prompt prompt = new RicherPrompt() {
 *     @Override
 *     public BaseComponent getRicherPromptText(ConversationContext context) {
 *         return new TextComponent(new ComponentBuilder("What's your name?")
 *                 .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ((Player)context.getForWhom()).getDisplayName()))
 *                 .create());
 *     }
 *
 *     @Override
 *     public boolean blocksForInput(@NotNull ConversationContext context) {
 *         return true;
 *     }
 *
 *     @Nullable
 *     @Override
 *     public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
 *         return END_OF_CONVERSATION;
 *     }
 * }
 *     }
 * </pre>
 * becomes
 * <pre>
 *     {@code
 *     Prompt prompt = (RicherShortPrompt) context -> new QuestionAndPrompt<>(new TextComponent(new ComponentBuilder("What's your name?")
 *         .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ((Player)context.getForWhom()).getDisplayName()))
 *         .create()),
 *         Prompt.END_OF_CONVERSATION);
 *     }
 * </pre>
 */
public interface RicherShortPrompt extends RicherPrompt {

    /**
     * Combines the results of a usual prompt into one type.
     * @param context Context information about the conversation.
     * @return the question as a {@link BaseComponent} and the next prompt to display.
     */
    public QuestionAndPrompt<BaseComponent> getShort(ConversationContext context);

    @Override
    default BaseComponent getRicherPromptText(ConversationContext context) {
        return getShort(context).t();
    }

    /**
     * In this case it always blocks for input.
     * @param context Context information about the conversation.
     * @return true
     */
    @Override
    default boolean blocksForInput(@NotNull ConversationContext context) {
        return true;
    }

    /**
     * Accepts the input without any validation for easy instantiation if no validation is needed.
     * @param context Context information about the conversation.
     * @param input The input from the user.
     * @return The next prompt to display defined in {@link QuestionAndPrompt#prompt()} from {@link RicherShortPrompt#getShort(ConversationContext)}.
     */
    @Nullable
    @Override
    default Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        return getShort(context).prompt();
    }
}
