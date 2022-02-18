package io.github.wand555.richerconversation.prompts;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

/**
 * Allows for more options for booleans also differentiating between true and false.
 */
public abstract class RicherBooleanPrompt extends BooleanPrompt {

    private final String[] acceptTrue;
    private final String[] acceptFalse;
    private final boolean useDefaults;

    /**
     * Default constructor to use the build in checks for booleans.
     */
    public RicherBooleanPrompt() {
        this(null, null, true);
    }

    /**
     * Constructs a prompt based on the given arrays.
     * @param acceptTrue Array for the accepting true values.
     * @param acceptFalse Array for the accepting false values.
     * @param useDefaults Whether to use the default values additionally.
     */
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
