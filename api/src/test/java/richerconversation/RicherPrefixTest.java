package richerconversation;

import io.github.wand555.richerconversation.RicherConversation;
import io.github.wand555.richerconversation.RicherConversationFactory;
import io.github.wand555.richerconversation.RicherPrefix;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class RicherPrefixTest {

    RicherConversationFactory rfc;

    @BeforeEach
    public void initialize() {
        rfc = new RicherConversationFactory(null);
    }

    @Test
    public void prefix() {
        rfc.withPrefix(context -> new TextComponent("Example"))
                .withFirstPrompt(new MessagePrompt() {
                    @Override
                    protected Prompt getNextPrompt(ConversationContext conversationContext) {
                        return END_OF_CONVERSATION;
                    }

                    @Override
                    public String getPromptText(ConversationContext conversationContext) {
                        return "Hello";
                    }
                });
        FakeConversable fakeConversable = new FakeConversable();
        Conversation conversation = rfc.buildConversation(fakeConversable);
        conversation.begin();
        assertEquals("ExampleHello", fakeConversable.lastSentMessage);
        conversation.abandon();

        //add an additional default prefix
        rfc.withPrefix((ConversationPrefix) conversationContext -> "Second");
        conversation = rfc.buildConversation(fakeConversable);
        conversation.begin();
        assertEquals("SecondHello", fakeConversable.lastSentMessage);
        conversation.abandon();

    }
}
