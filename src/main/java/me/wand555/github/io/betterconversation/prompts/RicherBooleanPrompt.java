package me.wand555.github.io.betterconversation.prompts;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

public abstract class RicherBooleanPrompt extends BooleanPrompt {

    private final String[] acceptTrue;
    private final String[] acceptFalse;
    private final boolean useDefaults;

    public RicherBooleanPrompt() {
        this(null, null, true);
    }

    public RicherBooleanPrompt(String[] acceptTrue, String[] acceptFalse, boolean useDefaults) {
        if(acceptTrue != null) {
            this.acceptTrue = acceptTrue;
        }
        else {
            this.acceptTrue = new String[] {"true", "on", "yes", "y", "1", "right", "correct", "valid"};
        }
        if(acceptFalse != null) {
            this.acceptFalse = acceptFalse;
        }
        else {
            this.acceptFalse = new String[] {"false", "off", "no", "n", "0", "wrong", "incorret", "invalid"};
        }
        this.useDefaults = useDefaults;
    }

    @Override
    protected boolean isInputValid(ConversationContext context, String input) {
        return (useDefaults && super.isInputValid(context, input)) || ArrayUtils.contains(acceptTrue, input) || ArrayUtils.contains(acceptFalse, input);
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext context, String input) {
        if(useDefaults) {
            //taken from source
            if(input.equalsIgnoreCase("y") || input.equals("1") || input.equalsIgnoreCase("right") || input.equalsIgnoreCase("correct") || input.equalsIgnoreCase("valid")) {
                input = "true";
            }
        }
        if(ArrayUtils.contains(acceptTrue, input)) {
            input = "true";
        }
        return acceptValidatedInput(context, BooleanUtils.toBoolean(input));
    }
}
