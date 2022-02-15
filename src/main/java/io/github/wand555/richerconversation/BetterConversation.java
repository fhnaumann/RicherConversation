package io.github.wand555.richerconversation;

import io.github.wand555.richerconversation.prompts.TestPrompt;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterConversation extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        Player player = Bukkit.getPlayer("wand555");
        BaseComponent[] components = new ComponentBuilder("This is a")
                .color(ChatColor.AQUA)
                .append("test")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "op wand555"))
                .create();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            new RicherConversationFactory(this)
                    .withFirstPrompt(new TestPrompt())
                    .buildConversation(player)
                    .begin();
        }, 10L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void test(TabCompleteEvent event) {
        System.out.println("fired");
    }
}
