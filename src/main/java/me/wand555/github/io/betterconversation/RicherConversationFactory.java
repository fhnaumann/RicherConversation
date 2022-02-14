package me.wand555.github.io.betterconversation;

import com.google.common.collect.ImmutableSet;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RicherConversationFactory extends ConversationFactory {

    private String cantGoBackMessage = "Cannot go back further!";
    private Set<String> goBackSequences;
    private Set<String> showHistorySequences;
    private BiFunction<PromptAndAnswer, ConversationContext, String> historyFormatting = (promptAndAnswer, context) -> "Q: " + promptAndAnswer.prompt().getPromptText(context) + " A: " + promptAndAnswer.answer();
    private Map<String, TriConsumer<ConversationContext, Deque<PromptAndAnswer>, Prompt>> customKeywords = new HashMap<>();

    /**
     * Constructs a ConversationFactory.
     *
     * @param plugin The plugin that owns the factory.
     */
    public RicherConversationFactory(Plugin plugin) {
        super(plugin);
    }


    public RicherConversationFactory withGoBack(String goBackSequence) {
        return withGoBack(Set.of(goBackSequence));
    }

    public RicherConversationFactory withGoBack(String goBackSequence, String cantGoBackMessage) {
        return withGoBack(Set.of(goBackSequence), cantGoBackMessage);
    }

    public RicherConversationFactory withGoBack(Set<String> goBackSequences) {
        return withGoBack(goBackSequences, cantGoBackMessage);
    }

    public RicherConversationFactory withGoBack(Set<String> goBackSequences, String cantGoBackMessage) {
        if(goBackSequences != null) {
            this.goBackSequences = ImmutableSet.copyOf(goBackSequences);
        }
        this.cantGoBackMessage = cantGoBackMessage;
        return this;
    }

    public RicherConversationFactory withShowHistory(String showHistorySequence) {
        return withShowHistory(Set.of(showHistorySequence));
    }

    public RicherConversationFactory withShowHistory(String showHistorySequence, BiFunction<PromptAndAnswer, ConversationContext, String> formatting) {
        return withShowHistory(Set.of(showHistorySequence), formatting);
    }

    public RicherConversationFactory withShowHistory(Set<String> showHistorySequences) {
        return withShowHistory(showHistorySequences, historyFormatting);
    }

    public RicherConversationFactory withShowHistory(Set<String> showHistorySequences, BiFunction<PromptAndAnswer, ConversationContext, String> formatting) {
        if(showHistorySequences != null) {
            this.showHistorySequences = ImmutableSet.copyOf(showHistorySequences);
        }
        this.historyFormatting = formatting;
        return this;
    }

    public RicherConversationFactory withCustomKeyword(String customKeyword, TriConsumer<ConversationContext, Deque<PromptAndAnswer>, Prompt> action) {
        customKeywords.put(customKeyword, action);
        return this;
    }

    @Override
    public Conversation buildConversation(Conversable forWhom) {
        //Abort conversation construction if we aren't supposed to talk to non-players
        if (playerOnlyMessage != null && !(forWhom instanceof Player)) {
            return new Conversation(plugin, forWhom, new Prompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return playerOnlyMessage;
                }

                @Override
                public boolean blocksForInput(ConversationContext context) {
                    return false;
                }

                @Override
                public Prompt acceptInput(ConversationContext context, String input) {
                    return END_OF_CONVERSATION;
                }
            });
        }

        //Clone any initial session data
        Map<Object, Object> copiedInitialSessionData = new HashMap<Object, Object>();
        copiedInitialSessionData.putAll(initialSessionData);

        //Build and return a conversation
        RicherConversation richerConversation = new RicherConversation(
                plugin,
                forWhom,
                firstPrompt,
                copiedInitialSessionData,
                goBackSequences,
                cantGoBackMessage,
                showHistorySequences,
                historyFormatting,
                customKeywords);

        try {
            Method modalMethod = Conversation.class.getDeclaredMethod("setModal", boolean.class);
            modalMethod.setAccessible(true);
            modalMethod.invoke(richerConversation, isModal);
        } catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {

        }
        //richerConversation.setModal(isModal);

        richerConversation.setLocalEchoEnabled(localEchoEnabled);

        try {
            Method prefixMethod = Conversation.class.getDeclaredMethod("setPrefix", ConversationPrefix.class);
            prefixMethod.setAccessible(true);
            prefixMethod.invoke(richerConversation, prefix);
        } catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {

        }

        //richerConversation.setPrefix(prefix);

        //Clone the conversation cancellers
        for (ConversationCanceller canceller : cancellers) {
            try {
                Method convCancellerMethod = Conversation.class.getDeclaredMethod("addConversationCanceller", ConversationCanceller.class);
                convCancellerMethod.setAccessible(true);
                convCancellerMethod.invoke(richerConversation, canceller);
            } catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {

            }

            //richerConversation.addConversationCanceller(canceller.clone());
        }

        //Add the ConversationAbandonedListeners
        for (ConversationAbandonedListener listener : abandonedListeners) {
            richerConversation.addConversationAbandonedListener(listener);
        }

        return richerConversation;
    }
}
