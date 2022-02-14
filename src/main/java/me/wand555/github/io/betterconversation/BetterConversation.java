package me.wand555.github.io.betterconversation;

import org.bukkit.Bukkit;
import org.bukkit.conversations.Conversation;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.function.BiConsumer;

public final class BetterConversation extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getScheduler().runTaskLater(this, () -> {
            new RicherConversationFactory(this)
                    .withGoBack("45")
                    .withShowHistory("history", (promptAndAnswer, context) -> "test" + promptAndAnswer.prompt().getPromptText(context))
                    .withCustomKeyword("test", (context, history, prompt) -> {
                        context.getForWhom().sendRawMessage("mittels custom keyword");
                        PromptAndAnswer popped = history.pop();
                        System.out.println("popped " + popped.prompt().getPromptText(context) + " from history");
                    })
                    .withFirstPrompt(new TestStartPrompt())
                    .buildConversation(Bukkit.getPlayer("wand555")).begin();
        }, 2L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
