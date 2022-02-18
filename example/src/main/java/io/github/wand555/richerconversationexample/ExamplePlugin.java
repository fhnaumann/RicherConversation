package io.github.wand555.richerconversationexample;


import io.github.wand555.richerconversation.RicherConversation;
import io.github.wand555.richerconversation.RicherConversationFactory;
import io.github.wand555.richerconversation.prompts.RicherBooleanPrompt;
import io.github.wand555.richerconversation.prompts.RicherPrompt;
import io.github.wand555.richerconversation.prompts.RicherShortPrompt;
import io.github.wand555.richerconversation.prompts.ShortPrompt;
import io.github.wand555.richerconversation.util.QuestionAndPrompt;
import io.github.wand555.richerconversationexample.prompts.BaseComponentPrompt;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        RicherConversation conversation = new RicherConversationFactory(this)
                .withPrefix(context -> new TextComponent(
                        new ComponentBuilder()
                                .append("[").color(ChatColor.of("#E27D60"))
                                .append("ExamplePlugin3").color(ChatColor.of("#41B3A3"))
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("You're engaged in a conversation with me!")))
                                .append("] ").color(ChatColor.of("#E27D60"))
                                .create()
                ))
                .withLocalEcho(true)
                .withModality(true)
                .withFirstPrompt(new BaseComponentPrompt())
                //.withFirstPrompt((RicherShortPrompt) context -> new QuestionAndPrompt<>())
                .withFirstPrompt((ShortPrompt) context -> new QuestionAndPrompt<>(
                        "My Question",
                        Prompt.END_OF_CONVERSATION
                ))
                .buildConversation(player);
        conversation.begin();
        return true;
    }
}
