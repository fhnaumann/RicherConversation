package io.github.wand555.richerconversation;

import io.github.wand555.richerconversation.prompts.RicherPrompt;
import io.github.wand555.richerconversation.util.PromptAndAnswer;
import io.github.wand555.richerconversation.util.TriConsumer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.conversations.ExactMatchConversationCanceller;
import org.bukkit.conversations.InactivityConversationCanceller;
import org.bukkit.conversations.NullConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A ConversationFactory is responsible for creating a {@link RicherConversation} from a predefined template.
 * A RicherConversationFactory is typically created when a plugin is instantiated and builds a {@link RicherConversation}
 * each time a user initiates a conversation with the plugin. Each {@link RicherConversation}
 * maintains its own state and calls back as needed into the plugin.
 * The {@link RicherConversationFactory} implements a fluid API, allowing parameters to be set as an extension to the constructor.
 */
public class RicherConversationFactory {

    /*
    Copied from ConversationFactory
     */
    protected Plugin plugin;
    protected boolean isModal;
    protected boolean localEchoEnabled;
    protected ConversationPrefix prefix;
    protected Prompt firstPrompt;
    protected Map<Object, Object> initialSessionData;
    protected String playerOnlyMessage;
    protected List<ConversationCanceller> cancellers;
    protected List<ConversationAbandonedListener> abandonedListeners;

    /*
     * Additional fields
     */
    private BaseComponent cantGoBackMessage;
    private final Set<String> goBackSequences;
    private final Set<String> showHistorySequences;
    private BaseComponentFormatter historyFormatting;
    private final Map<String, TriConsumer<ConversationContext, Deque<PromptAndAnswer>, Prompt>> customKeywords;

    /**
     * Constructs a RicherConversationFactory.
     *
     * @param plugin The plugin that owns the factory.
     */
    public RicherConversationFactory(Plugin plugin) {
        this.plugin = plugin;
        this.isModal = true;
        this.localEchoEnabled = true;
        this.prefix = new NullConversationPrefix();
        this.firstPrompt = Prompt.END_OF_CONVERSATION;
        this.initialSessionData = new HashMap<>();
        this.playerOnlyMessage = null;
        this.cancellers = new ArrayList<>();
        this.abandonedListeners = new ArrayList<>();

        this.cantGoBackMessage = new TextComponent("Cannot go back further!");
        this.goBackSequences = new HashSet<>();
        this.showHistorySequences = new HashSet<>();
        this.historyFormatting = (promptAndAnswer, context) -> new TextComponent("Q: " + promptAndAnswer.prompt().getPromptText(context) + " A: " + promptAndAnswer.answer());
        this.customKeywords = new HashMap<>();
    }

    /**
     * Sets the modality of all {@link Conversation}s created by this factory.
     * If a conversation is modal, all messages directed to the player are
     * suppressed for the duration of the conversation.
     * <p>
     * The default is True.
     *
     * @param modal The modality of all conversations to be created.
     * @return This object.
     */
    @NotNull
    public RicherConversationFactory withModality(boolean modal) {
        this.isModal = modal;
        return this;
    }

    /**
     * Sets the local echo status for all {@link Conversation}s created by
     * this factory. If local echo is enabled, any text submitted to a
     * conversation gets echoed back into the submitter's chat window.
     *
     * @param localEchoEnabled The status of local echo.
     * @return This object.
     */
    @NotNull
    public RicherConversationFactory withLocalEcho(boolean localEchoEnabled) {
        this.localEchoEnabled = localEchoEnabled;
        return this;
    }

    /**
     *
     * @param prefix
     * @return
     */
    public RicherConversationFactory withPrefix(RicherPrefix prefix) {
        this.prefix = prefix;
        return this;
    }

    /**
     * Sets the {@link ConversationPrefix} that prepends all output from all
     * generated conversations.
     * <p>
     * The default is a {@link NullConversationPrefix};
     *
     * @param prefix The ConversationPrefix to use.
     * @return This object.
     */
    public RicherConversationFactory withPrefix(ConversationPrefix prefix) {
        this.prefix = prefix;
        return this;
    }

    /**
     * Sets the number of inactive seconds to wait before automatically
     * abandoning all generated conversations.
     * <p>
     * The default is 600 seconds (5 minutes).
     *
     * @param timeoutSeconds The number of seconds to wait.
     * @return This object.
     */
    @NotNull
    public RicherConversationFactory withTimeout(int timeoutSeconds) {
        return this.withConversationCanceller(new InactivityConversationCanceller(this.plugin, timeoutSeconds));
    }

    /**
     * Sets the first prompt to use in all generated conversations.
     * <p>
     * The default is Prompt.END_OF_CONVERSATION.
     *
     * @param firstPrompt The first prompt.
     * @return This object.
     */
    @NotNull
    public RicherConversationFactory withFirstPrompt(@Nullable Prompt firstPrompt) {
        this.firstPrompt = firstPrompt;
        return this;
    }

    /**
     * Sets any initial data with which to populate the conversation context
     * sessionData map.
     *
     * @param initialSessionData The conversation context's initial
     *     sessionData.
     * @return This object.
     */
    @NotNull
    public RicherConversationFactory withInitialSessionData(@NotNull Map<Object, Object> initialSessionData) {
        this.initialSessionData = initialSessionData;
        return this;
    }

    /**
     * Sets the player input that, when received, will immediately terminate
     * the conversation.
     *
     * @param escapeSequence Input to terminate the conversation.
     * @return This object.
     */
    @NotNull
    public RicherConversationFactory withEscapeSequence(@NotNull String escapeSequence) {
        return this.withConversationCanceller(new ExactMatchConversationCanceller(escapeSequence));
    }

    /**
     * Adds a {@link ConversationCanceller} to constructed conversations.
     *
     * @param canceller The {@link ConversationCanceller} to add.
     * @return This object.
     */
    @NotNull
    public RicherConversationFactory withConversationCanceller(@NotNull ConversationCanceller canceller) {
        this.cancellers.add(canceller);
        return this;
    }

    /**
     * Prevents this factory from creating a conversation for non-player
     * {@link Conversable} objects.
     *
     * @param playerOnlyMessage The message to return to a non-play in lieu of
     *     starting a conversation.
     * @return This object.
     */
    @NotNull
    public RicherConversationFactory thatExcludesNonPlayersWithMessage(@Nullable String playerOnlyMessage) {
        this.playerOnlyMessage = playerOnlyMessage;
        return this;
    }

    /**
     * Adds a {@link ConversationAbandonedListener} to all conversations
     * constructed by this factory.
     *
     * @param listener The listener to add.
     * @return This object.
     */
    @NotNull
    public RicherConversationFactory addConversationAbandonedListener(@NotNull ConversationAbandonedListener listener) {
        this.abandonedListeners.add(listener);
        return this;
    }

    /**
     * Sets the player input that, when received, will cause the conversation flow to move to the previous step.
     * @param goBackSequence Input to trigger the effect.
     * @return This object.
     */
    public RicherConversationFactory withGoBack(String goBackSequence) {
        return withGoBack(goBackSequence, cantGoBackMessage);
    }

    /**
     * Sets the player input that, when received, will cause the conversation flow to move to the previous step.
     * @param goBackSequence Input to trigger the effect.
     * @param cantGoBackMessage Message to be displayed if the beginning of the conversation is reached and therefore cannot go back any further.
     * @return This object.
     */
    public RicherConversationFactory withGoBack(String goBackSequence, String cantGoBackMessage) {
        return withGoBack(goBackSequence, new TextComponent(cantGoBackMessage));
    }

    /**
     * Sets the player input that, when received, will cause the conversation flow to move to the previous step.
     * @param goBackSequence Input to trigger the effect.
     * @param cantGoBackMessage Message to be displayed if the beginning of the conversation is reached and therefore cannot go back any further.
     * @return This object.
     */
    public RicherConversationFactory withGoBack(String goBackSequence, BaseComponent cantGoBackMessage) {
        goBackSequences.add(goBackSequence);
        this.cantGoBackMessage = cantGoBackMessage;
        return this;
    }

    /**
     * Sets the player input that, when received, will display the entire previous history.
     * Typing this keyword won't have any effect on the current prompt. It behaves like no answer was given.
     * @param showHistorySequence Input to trigger the effect.
     * @return This object.
     */
    public RicherConversationFactory withShowHistory(String showHistorySequence) {
        return withShowHistory(showHistorySequence, historyFormatting);
    }

    /**
     * Sets the player input that, when received, will display the entire previous history.
     * Typing this keyword won't have any effect on the current prompt. It behaves like no answer was given.
     * @param showHistorySequence Input to trigger the effect.
     * @param formatting Function to format each line in the history with.
     * @return This object.
     */
    public RicherConversationFactory withShowHistory(String showHistorySequence, StringFormatter formatting) {
        return withShowHistory(showHistorySequence, (BaseComponentFormatter) (promptAndAnswer, context) -> new TextComponent(formatting.apply(promptAndAnswer, context)));
    }

    /**
     * Sets the player input that, when received, will display the entire previous history.
     * Typing this keyword won't have any effect on the current prompt. It behaves like no answer was given.
     * @param showHistorySequence Input to trigger the effect.
     * @param formatting Function to format each line in the history with.
     * @return This object.
     */
    public RicherConversationFactory withShowHistory(String showHistorySequence, BaseComponentFormatter formatting) {
        showHistorySequences.add(showHistorySequence);
        this.historyFormatting = formatting;
        return this;
    }

    /**
     * Sets a player input that, when received, will trigger a custom action.
     * The action consumer provides the {@link ConversationContext}, the entire history and the current prompt that is displayed
     * while the keyword was typed.
     * <p></p>
     * The history stack is a copy of the underlying history. Any changes to this copy are NOT reflected in the actual history.
     * <p></p>
     * An important note for the history: The current prompt is not present in the history stack. So for example by removing the top
     * element from the stack you actually remove the previous prompt from the history, not the currently displayed one.
     * @param customKeyword Input to trigger the action.
     * @param action Action to be performed after the keyword is typed.
     * @return This object.
     */
    public RicherConversationFactory withCustomKeyword(String customKeyword, TriConsumer<ConversationContext, Deque<PromptAndAnswer>, Prompt> action) {
        customKeywords.put(customKeyword, action);
        return this;
    }

    /**
     * Constructs a {@link Conversation} in accordance with the defaults set
     * for this factory.
     *
     * @param forWhom The entity for whom the new conversation is mediating.
     * @return A new conversation.
     */
    public RicherConversation buildConversation(Conversable forWhom) {
        //Abort conversation construction if we aren't supposed to talk to non-players
        if (playerOnlyMessage != null && !(forWhom instanceof Player)) {
            return new RicherConversation(plugin, forWhom, new Prompt() {
                @NotNull
                @Override
                public String getPromptText(@NotNull ConversationContext context) {
                    return playerOnlyMessage;
                }

                @Override
                public boolean blocksForInput(@NotNull ConversationContext context) {
                    return false;
                }

                @Nullable
                @Override
                public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
                    return END_OF_CONVERSATION;
                }
            });
        }

        //Clone any initial session data
        Map<Object, Object> copiedInitialSessionData = new HashMap<>(initialSessionData);
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

        //use reflection because the methods are made private (for no apparent reason, why would the variables be protected then?)
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
            prefixMethod.invoke(richerConversation, this.prefix);
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
