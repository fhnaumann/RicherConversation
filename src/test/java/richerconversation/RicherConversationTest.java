package richerconversation;

import me.wand555.github.io.betterconversation.AgePrompt;
import me.wand555.github.io.betterconversation.BetterConversation;
import me.wand555.github.io.betterconversation.PromptAndAnswer;
import me.wand555.github.io.betterconversation.RicherConversation;
import me.wand555.github.io.betterconversation.RicherConversationFactory;
import me.wand555.github.io.betterconversation.TestStartPrompt;
import org.bukkit.Bukkit;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.Prompt;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RicherConversationTest {

    private Prompt testStartPrompt;
    private RicherConversation conversation;

    @BeforeEach
    public void initialize() {
        testStartPrompt = new TestStartPrompt();
        conversation = (RicherConversation) new RicherConversationFactory(null)
                .withGoBack("back")
                .withShowHistory("history", (promptAndAnswer, context) -> "test" + promptAndAnswer.prompt().getPromptText(context))
                .withCustomKeyword("test", (context, history, prompt) -> {
                    context.getForWhom().sendRawMessage("mittels custom keyword");
                    PromptAndAnswer popped = history.pop();
                    System.out.println("popped " + popped.prompt().getPromptText(context) + " from history");
                })
                .withFirstPrompt(testStartPrompt)
                .buildConversation(new FakeConversable());
    }

    @Test
    public void backKeyword() {
        conversation.begin();
        conversation.acceptInput("true");
        conversation.acceptInput("back");
        assertEquals(testStartPrompt, conversation.getCurrentPrompt());
    }

    private static class FakeConversable implements Conversable {

        public String lastSentMessage;
        public Prompt currentPrompt;

        @Override
        public boolean isConversing() {
            return false;
        }

        @Override
        public void acceptConversationInput(String input) {

        }

        @Override
        public boolean beginConversation(Conversation conversation) {
            conversation.outputNextPrompt();
            return true;
        }

        @Override
        public void abandonConversation(Conversation conversation) {

        }

        @Override
        public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {

        }

        @Override
        public void sendRawMessage(String message) {
            lastSentMessage = message;
        }

        @Override
        public void sendRawMessage(UUID sender, String message) {
            sendRawMessage(message);
        }
    }
}
