package io.github.wand555.richerconversationexample;

import io.github.wand555.richerconversation.RicherConversation;
import io.github.wand555.richerconversation.RicherConversationFactory;
import io.github.wand555.richerconversationexample.prompts.BaseComponentPrompt;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        getCommand("showbasecomponentsupport").setExecutor(this);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        Conversation conversation = new RicherConversationFactory(this)
                .withLocalEcho(true)
                .withModality(true)
                .withPrefix(context -> ChatColor.of("#E27D60") + "[" + ChatColor.of("#41B3A3") + "ExamplePlugin" + ChatColor.of("#E27D60") + "] ")
                .withFirstPrompt(new BaseComponentPrompt())
                .buildConversation(player);
        conversation.begin();
        return true;
    }
}
