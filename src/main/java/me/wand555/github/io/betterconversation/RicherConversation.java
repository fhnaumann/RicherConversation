package me.wand555.github.io.betterconversation;

import me.wand555.github.io.betterconversation.prompts.RicherPrompt;
import me.wand555.github.io.betterconversation.util.PromptAndAnswer;
import me.wand555.github.io.betterconversation.util.TriConsumer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * The Conversation class is responsible for tracking the current state of a
 * conversation, displaying prompts to the user, and dispatching the user's
 * response to the appropriate place. Conversation objects are not typically
 * instantiated directly. Instead a {@link RicherConversationFactory} is used to
 * construct identical conversations on demand.
 * <p>
 * Conversation flow consists of a directed graph of {@link Prompt} objects.
 * Each time a prompt gets input from the user, it must return the next prompt
 * in the graph. Since each Prompt chooses the next Prompt, complex
 * conversation trees can be implemented where the nature of the player's
 * response directs the flow of the conversation.
 * <p>
 * Each conversation has a {@link ConversationPrefix} that prepends all output
 * from the conversation to the player. The ConversationPrefix can be used to
 * display the plugin name or conversation status as the conversation evolves.
 * <p>
 * Each conversation has a timeout measured in the number of inactive seconds
 * to wait before abandoning the conversation. If the inactivity timeout is
 * reached, the conversation is abandoned and the user's incoming and outgoing
 * chat is returned to normal.
 * <p>
 * You should not construct a conversation manually. Instead, use the {@link
 * RicherConversationFactory} for access to all available options.
 */
public class RicherConversation extends Conversation {

    //store again because it has private access
    private Prompt firstPrompt;

    private Map<String, Supplier<? extends Prompt>> keywords;
    private Set<String> goBackSequences;
    private String cantGoBackSequence;
    private Set<String> historySequences;
    private BiFunction<PromptAndAnswer, ConversationContext, String> historyFormatting;
    private Map<String, TriConsumer<ConversationContext, Deque<PromptAndAnswer>, Prompt>> customKeywords;

    private Deque<PromptAndAnswer> history = new ArrayDeque<>();

    /**
     * Initializes a new, richer Conversation.
     * @param plugin The plugin that owns this conversation.
     * @param forWhom The entity for whom this conversation is mediating.
     * @param firstPrompt The first prompt in the conversation graph
     * @param initialSessionData Any initial values to put in the conversation context sessionData map.
     * @param goBackSequences keywords which all cause to move back to the previous step in the graph.
     * @param cantGoBackSequence message to display if the user tries to go back while being at the beginning.
     * @param historySequences keywords which all cause the previous questions and answers to be displayed.
     * @param historyFormatting formatting to apply for history.
     * @param customKeywords any additional keywords with a custom action following.
     */
    public RicherConversation(
            Plugin plugin,
            Conversable forWhom,
            Prompt firstPrompt,
            Map<Object, Object> initialSessionData,
            Set<String> goBackSequences,
            String cantGoBackSequence,
            Set<String> historySequences,
            BiFunction<PromptAndAnswer, ConversationContext, String> historyFormatting,
            Map<String, TriConsumer<ConversationContext, Deque<PromptAndAnswer>, Prompt>> customKeywords) {
        super(plugin, forWhom, firstPrompt, initialSessionData);
        this.firstPrompt = firstPrompt;
        this.goBackSequences = goBackSequences;
        this.historySequences = historySequences;
        this.cantGoBackSequence = cantGoBackSequence;
        this.historyFormatting = historyFormatting;
        this.customKeywords = customKeywords;
    }

    @Override
    public void abandon() {
        super.abandon();
        history.clear();
    }

    @Override
    public synchronized void abandon(ConversationAbandonedEvent details) {
        super.abandon(details);
        history.clear();
    }

    @Override
    public void outputNextPrompt() {
        if (currentPrompt == null) {
            abandon(new ConversationAbandonedEvent(this));
        } else {
            if(context.getForWhom() instanceof Player player && currentPrompt instanceof RicherPrompt richerPrompt) {
                BaseComponent[] message = new ComponentBuilder(prefix.getPrefix(context))
                        .append(richerPrompt.getRicherPromptText(context)).create();
                player.spigot().sendMessage(message);
            }
            else {
                context.getForWhom().sendRawMessage(prefix.getPrefix(context) + currentPrompt.getPromptText(context));
            }
            if (!currentPrompt.blocksForInput(context)) {
                history.push(new PromptAndAnswer(currentPrompt, null));
                currentPrompt = currentPrompt.acceptInput(context, null);
                outputNextPrompt();
            }
        }
    }

    /**
     * Passes player input into the current prompt. The next prompt (as determined by the current prompt) is then displayed to the user.
     * If a keyword is typed the appropriate action will take place instead.
     * @param input The user's chat text.
     */
    @Override
    public void acceptInput(String input) {
        if (currentPrompt != null) {
            // Echo the user's input
            if (localEchoEnabled) {
                context.getForWhom().sendRawMessage(prefix.getPrefix(context) + input);
            }
            // Test for conversation abandonment based on input
            for (ConversationCanceller canceller : cancellers) {
                if (canceller.cancelBasedOnInput(context, input)) {
                    abandon(new ConversationAbandonedEvent(this, canceller));
                    return;
                }
            }
            if(goBackSequences.contains(input)) {
                goBack();
            }
            else if(historySequences.contains(input)) {
                formatToReadableHistory().forEach(context.getForWhom()::sendRawMessage);
                return;
            }
            else if(customKeywords.containsKey(input)) {
                customKeywords.get(input).accept(context, new ArrayDeque<>(history), currentPrompt);
                //name is misleading, because we're showing (possibly) the same prompt
                outputNextPrompt();
                return;
            }
            else {
                history.push(new PromptAndAnswer(currentPrompt, input));
            }
            // Not abandoned, output the next prompt
            currentPrompt = currentPrompt.acceptInput(context, input);
            outputNextPrompt();
        }
    }

    /**
     * Going back is only possible on prompts that require user input.
     * Non-input prompts are skipped when going back.
     * Impl detail: If there are only non-input prompts in the history, going back means going to the first prompt of the conversation.
     */
    private void goBack() {
        if(!history.isEmpty()) {
            currentPrompt = history.pop().prompt();
            //keep going back until we find a prompt that requires input from the user or we reach the start of the conversation
            while(!currentPrompt.blocksForInput(context) && !history.isEmpty()) {
                currentPrompt = history.pop().prompt();
            }
        }
        else {
            context.getForWhom().sendRawMessage(prefix.getPrefix(context) + cantGoBackSequence);
        }

    }

    private List<String> formatToReadableHistory() {
        return history.stream()
                .map(promptAndAnswer -> historyFormatting.apply(promptAndAnswer, context))
                .collect(Collectors.toList());
    }

    public Prompt getCurrentPrompt() {
        return currentPrompt;
    }


}
