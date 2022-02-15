package richerconversation;

import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.Prompt;

import java.util.UUID;

class FakeConversable implements Conversable {

    String lastSentMessage;
    Prompt currentPrompt;

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
        //ignore null explicitly when testing to cantGoBackMessage, because that's what each test prompt returns as a question
        //and the order is:
        //"null" is printed (FirstPrompt) -> "back" input -> "Can't go back!" is printed -> "null" is printed (FirstPrompt again)
        if(message != null && !message.equals("null")) {
            lastSentMessage = message;
        }
    }

    @Override
    public void sendRawMessage(UUID sender, String message) {
        sendRawMessage(message);
    }
}
