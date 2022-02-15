package richerconversation;

import me.wand555.github.io.betterconversation.prompts.RicherBooleanPrompt;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RicherBooleanPromptTest {


    @Test
    public void richerBooleanPromptDefault() {
        RicherBooleanPromptImpl prompt = new RicherBooleanPromptImpl();
        FakeConversable fakeConversable = new FakeConversable();
        Conversation conversation = new ConversationFactory(null)
                .withFirstPrompt(prompt)
                .buildConversation(fakeConversable);
        conversation.begin();

        assertFalse(prompt.isInputValid(conversation.getContext(), "apple"));
        assertFalse(prompt.isInputValid(conversation.getContext(), "banana"));
        assertTrue(prompt.isInputValid(conversation.getContext(), "true"));
        assertTrue(prompt.isInputValid(conversation.getContext(), "y"));
        assertTrue(prompt.isInputValid(conversation.getContext(), "n"));
    }

    @Test
    public void richerBooleanPromptEnhancedWithDefaults() {
        RicherBooleanPromptImpl prompt = new RicherBooleanPromptImpl(new String[] {"apple"}, new String[] {"banana"}, true);
        FakeConversable fakeConversable = new FakeConversable();
        Conversation conversation = new ConversationFactory(null)
                .withFirstPrompt(prompt)
                .buildConversation(fakeConversable);
        conversation.begin();

        assertTrue(prompt.isInputValid(conversation.getContext(), "apple"));
        assertTrue(prompt.isInputValid(conversation.getContext(), "banana"));
        assertTrue(prompt.isInputValid(conversation.getContext(), "true"));
        assertTrue(prompt.isInputValid(conversation.getContext(), "false"));
    }

    @Test
    public void richerBooleanPromptEnhancedWithoutDefaults() {
        RicherBooleanPromptImpl prompt = new RicherBooleanPromptImpl(new String[] {"apple"}, new String[] {"banana"}, false);
        FakeConversable fakeConversable = new FakeConversable();
        Conversation conversation = new ConversationFactory(null)
                .withFirstPrompt(prompt)
                .buildConversation(fakeConversable);
        conversation.begin();

        assertTrue(prompt.isInputValid(conversation.getContext(), "apple"));
        assertTrue(prompt.isInputValid(conversation.getContext(), "banana"));
        assertFalse(prompt.isInputValid(conversation.getContext(), "true"));
        assertFalse(prompt.isInputValid(conversation.getContext(), "false"));
    }



    private class RicherBooleanPromptImpl extends RicherBooleanPrompt {

        public RicherBooleanPromptImpl() {
            super();
        }

        public RicherBooleanPromptImpl(String[] acceptTrue, String[] acceptFalse, boolean useDefaults) {
            super(acceptTrue, acceptFalse, useDefaults);
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, boolean input) {
            return END_OF_CONVERSATION;
        }

        @Override
        public String getPromptText(ConversationContext context) {
            return null;
        }

        //change visibility from protected to public
        @Override
        public boolean isInputValid(ConversationContext context, String input) {
            return super.isInputValid(context, input);
        }
    }
}
