package io.github.wand555.richerconversationexample;


import io.github.wand555.richerconversation.RicherConversation;
import io.github.wand555.richerconversation.RicherConversationFactory;
import io.github.wand555.richerconversation.RicherPrefix;
import io.github.wand555.richerconversation.prompts.RicherBooleanPrompt;
import io.github.wand555.richerconversation.prompts.RicherPrompt;
import io.github.wand555.richerconversation.prompts.RicherShortPrompt;
import io.github.wand555.richerconversation.prompts.ShortPrompt;
import io.github.wand555.richerconversation.util.QuestionAndPrompt;
import io.github.wand555.richerconversationexample.prompts.BaseComponentPrompt;
import io.github.wand555.richerconversationexample.prompts.NamePrompt;
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
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExamplePlugin extends JavaPlugin implements CommandExecutor {

    public static ChatColor MAIN_COLOR = ChatColor.of("#845ec2");
    public static ChatColor SECONDARY_MAIN_COLOR = ChatColor.of("#ff9671");
    public static ChatColor HIGHLIGHT_COLOR = ChatColor.of("#d65db1");
    public static ChatColor QUESTION_COLOR = ChatColor.of("#FFC75F");


    @Override
    public void onEnable() {
        getCommand("showbasecomponentsupport").setExecutor(this);
        getCommand("showgoback").setExecutor(this);
        getCommand("showhistory").setExecutor(this);
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

        if(command.getName().equals("showbasecomponentsupport")) {

        }
        else if(command.getName().equals("showgoback")) {
            RicherConversation conversation = new RicherConversationFactory(this)
                    //define a prefix using BaseComponents
                    .withPrefix(context -> new TextComponent(new ComponentBuilder()
                            .append("[")
                            .color(SECONDARY_MAIN_COLOR)
                            .append("ExamplePlugin")
                            .color(MAIN_COLOR)
                            .append("] ")
                            .color(SECONDARY_MAIN_COLOR)
                            .create()))
                    //define a go-back keyword
                    .withGoBack("back", new TextComponent(new ComponentBuilder("Can't go back.")
                            .color(SECONDARY_MAIN_COLOR)
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(new ComponentBuilder("No, really!")
                                    .color(HIGHLIGHT_COLOR)
                                    .create())))
                            .create()))
                    .withGoBack("undo", new TextComponent(new ComponentBuilder("Sorry, can't undo the first step.")
                            .color(SECONDARY_MAIN_COLOR)
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(new ComponentBuilder("It's just not undoable...")
                                    .color(HIGHLIGHT_COLOR)
                                    .create())))
                            .create()))
                    //define first prompt
                    .withFirstPrompt(new NamePrompt())
                    //building conversation
                    .buildConversation(player);
            conversation.begin();
        }


        return true;
    }
}
