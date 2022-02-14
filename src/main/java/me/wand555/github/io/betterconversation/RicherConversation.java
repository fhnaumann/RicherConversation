package me.wand555.github.io.betterconversation;

import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ManuallyAbandonedConversationCanceller;
import org.bukkit.conversations.Prompt;
import org.bukkit.craftbukkit.v1_18_R1.conversations.ConversationTracker;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.plugin.Plugin;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    public RicherConversation(
            Plugin plugin,
            Conversable forWhom,
            Prompt firstPrompt,
            Set<String> goBackSequences,
            String cantGoBackSequence,
            Set<String> historySequences,
            BiFunction<PromptAndAnswer, ConversationContext, String> historyFormatting,
            Map<String, TriConsumer<ConversationContext, Deque<PromptAndAnswer>, Prompt>> customKeywords) {
        super(plugin, forWhom, firstPrompt);
        this.firstPrompt = firstPrompt;
        this.goBackSequences = goBackSequences;
        this.historySequences = historySequences;
        this.cantGoBackSequence = cantGoBackSequence;
        this.historyFormatting = historyFormatting;
        this.customKeywords = customKeywords;
    }

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
                customKeywords.get(input).accept(context, history, currentPrompt);
                //might have modified the history deque
                if(!history.isEmpty()) {
                    System.out.println("history not empty");
                    currentPrompt = history.getFirst().prompt();
                    //edge case: clear last remaining element, if current prompt is actually the prompt from the beginning
                    if(currentPrompt == firstPrompt) {
                        history.clear();
                    }
                }
                //removed all elements, start at the beginning again
                else {
                    currentPrompt = firstPrompt;
                }
                System.out.println(currentPrompt);
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

    private void goBack() {
        if(!history.isEmpty()) {
            currentPrompt = null;
            currentPrompt = history.pop().prompt();
            System.out.println("prompt: " + currentPrompt.getPromptText(context) + "blocks? " + currentPrompt.blocksForInput(context));
            while(!currentPrompt.blocksForInput(context)) {
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


}
