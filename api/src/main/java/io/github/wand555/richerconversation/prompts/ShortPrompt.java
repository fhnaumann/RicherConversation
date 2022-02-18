package io.github.wand555.richerconversation.prompts;

import io.github.wand555.richerconversation.util.QuestionAndPrompt;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Creates a prompt. This interface combines the return values from {@link Prompt#getPromptText(ConversationContext)}
 * and {@link Prompt#acceptInput(ConversationContext, String)} into one type in order to be created
 * with lambda syntax.
 * It is useful when the prompt requires an input from the user which doesn't have to validated.
 * So
 * <pre>
 *     {@code
 *     Prompt prompt = new StringPrompt() {
 *         @NotNull
 *         @Override
 *         public String getPromptText(@NotNull ConversationContext context) {
 *             return "What's your name?";
 *         }
 *
 *         @Nullable
 *         @Override
 *         public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
 *             return END_OF_CONVERSATION;
 *        }
 *     }
 *     }
 * </pre>
 * becomes
 * <pre>
 *     {@code
 *     Prompt prompt = (ShortPrompt) context -> new QuestionAndPrompt<>("What's your name?", Prompt.END_OF_CONVERSATION);
 *     }
 * </pre>
 */
public interface ShortPrompt extends Prompt {

    /**
     * Combines the results of a usual prompt into one type.
     * @param context Context information about the conversation.
     * @return the question as a string and the next prompt to display.
     */
    public QuestionAndPrompt<String> getShort(ConversationContext context);

    @NotNull
    @Override
    default String getPromptText(@NotNull ConversationContext context) {
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
     * @return The next prompt to display defined in {@link QuestionAndPrompt#prompt()} from {@link ShortPrompt#getShort(ConversationContext)}.
     */
    @Nullable
    @Override
    default Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        return getShort(context).prompt();
    }
}
