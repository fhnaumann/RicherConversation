package richerconversation;

import me.wand555.github.io.betterconversation.util.PromptAndAnswer;
import me.wand555.github.io.betterconversation.RicherConversation;
import me.wand555.github.io.betterconversation.RicherConversationFactory;
import me.wand555.github.io.betterconversation.util.TriConsumer;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Deque;
import java.util.UUID;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

public class RicherConversationTest {

    private Prompt firstPrompt;
    private Prompt secondPrompt;
    private Prompt thirdPrompt;
    private String goBackKeyword = "back";
    private String cantGoBackMessage = "Can't go back!";
    private String historyKeyword = "history";
    private BiFunction<PromptAndAnswer, ConversationContext, String> historyFormatting = (promptAndAnswer, context) -> "Q: " + promptAndAnswer.prompt().getPromptText(context) + " A: " + promptAndAnswer.answer();
    private String customKeyword1 = "test1";
    private String actionMessage1 = "from action!";
    private TriConsumer<ConversationContext, Deque<PromptAndAnswer>, Prompt> customAction1 = (context, history, prompt) -> context.getForWhom().sendRawMessage(actionMessage1);

    private RicherConversation conversation;
    private FakeConversable fakeConversable;

    @BeforeEach
    public void initialize() {
        fakeConversable = new FakeConversable();
        firstPrompt = new FirstPrompt();
        secondPrompt = new SecondPrompt();
        thirdPrompt = new ThirdPrompt();
        conversation = (RicherConversation) new RicherConversationFactory(null)
                .withGoBack(goBackKeyword, cantGoBackMessage)
                .withShowHistory(historyKeyword, historyFormatting)
                .withCustomKeyword(customKeyword1, customAction1)
                .withFirstPrompt(firstPrompt)
                .buildConversation(fakeConversable);
    }

    @Test
    public void backKeyword() {
        conversation.begin();
        conversation.acceptInput("1");
        conversation.acceptInput(goBackKeyword);
        assertEquals(firstPrompt, conversation.getCurrentPrompt());
        conversation.abandon();

        conversation.begin();
        conversation.acceptInput("1");
        conversation.acceptInput("1");
        conversation.acceptInput(goBackKeyword);
        assertEquals(secondPrompt, conversation.getCurrentPrompt());
        conversation.abandon();

        conversation.begin();
        conversation.acceptInput(goBackKeyword);
        assertEquals(cantGoBackMessage, fakeConversable.lastSentMessage);
        conversation.abandon();
    }

    @Test
    public void historyKeyword() {
        conversation.begin();
        conversation.acceptInput("1");
        conversation.acceptInput("history");
        assertEquals(historyFormatting.apply(new PromptAndAnswer(firstPrompt, "1"), conversation.getContext()), fakeConversable.lastSentMessage);
        assertEquals(secondPrompt, conversation.getCurrentPrompt());
        conversation.abandon();
    }

    @Test
    public void testCustomKeyword() {
        conversation.begin();
        conversation.acceptInput("1");
        conversation.acceptInput(customKeyword1);
        assertEquals(actionMessage1, fakeConversable.lastSentMessage);
        assertEquals(secondPrompt, conversation.getCurrentPrompt());
        conversation.abandon();
    }

    private class FirstPrompt extends BooleanPrompt {

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, boolean input) {
            return secondPrompt;
        }

        @Override
        public String getPromptText(ConversationContext context) {
            return null;
        }
    }

    private class SecondPrompt extends BooleanPrompt {

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, boolean input) {
            return thirdPrompt;
        }

        @Override
        public String getPromptText(ConversationContext context) {
            return null;
        }
    }

    private class ThirdPrompt extends BooleanPrompt {

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, boolean input) {
            return END_OF_CONVERSATION;
        }

        @Override
        public String getPromptText(ConversationContext context) {
            return null;
        }
    }
}
