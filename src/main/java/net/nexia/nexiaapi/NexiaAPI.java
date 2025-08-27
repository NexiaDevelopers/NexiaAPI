package net.nexia.nexiaapi;

import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.dialog.DialogBase;
import net.md_5.bungee.api.dialog.NoticeDialog;
import net.md_5.bungee.api.dialog.action.*;
import net.md_5.bungee.api.dialog.body.PlainMessageBody;
import net.md_5.bungee.api.dialog.input.*;
import net.nexia.nexiaapi.command.CommandHandler;
import net.nexia.nexiaapi.command.PlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class NexiaAPI extends JavaPlugin implements CommandExecutor
{
    @Override
    public void onEnable() {

        CommandHandler ch = new CommandHandler(this);

        ch.addCommand(new PlayerCommand("guppy") {{
            setFunction((sender, args) -> test((Player)sender));
        }});

        getCommand("form").setExecutor(this);

    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        for (String arg : args) {
            commandSender.sendMessage(arg);
        }
        return true;
    }

    public void test(Player player) {

        NoticeDialog noticeDialog = new NoticeDialog(
                new DialogBase(new TextComponent("testing 123")),
                new ActionButton(
                        new TextComponent("sell"),
                        new TextComponent("you have no choice btw"), 50,
                        new RunCommandAction("form \"number_key:$(number_key)\" \"single_option:$(single_option)\" \"string_key1:$(string_key1)\" \"string_key2:$(string_key2)\""))
//        new StaticAction(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.playnexia.net/"))
                );
        noticeDialog.getBase().canCloseWithEscape(false);

        TextComponent titleExtras = new TextComponent(" AAAAAA");
        titleExtras.setBold(true);
        titleExtras.setColor(ChatColor.DARK_RED.asBungee());

        noticeDialog.getBase().title().addExtra(titleExtras);

        noticeDialog.getBase().body(new ArrayList<>() {{
            add(new PlainMessageBody(new TextComponent("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum"), 300));
        }});

        noticeDialog.getBase().inputs(new ArrayList<>() {{
            add(new NumberRangeInput("number_key", new TextComponent("fancy numbers"), 0.0f,1.0f, 0.1f, 0.0f));
            add(new SingleOptionInput("single_option", new TextComponent("pick one"),
                    new InputOption("1", new TextComponent("Option 1"), true),
                    new InputOption("2", new TextComponent("Option 2"), false),
                    new InputOption("3", new TextComponent("Option 3"), false)
            ));
            add(new TextInput("string_key1", 300, new TextComponent("whats your name?"), true, "", 30));
            add(new TextInput("string_key2", 300, new TextComponent("tell a story!"), true, "once upon a time", 100, new TextInput.Multiline(3, 40)));
            add(new BooleanInput("bool_key", new TextComponent("you agree to sell your soul"), false, "agreed", "disagreed"));
        }});

        player.showDialog(noticeDialog);

    }

    @Override
    public void onDisable() {
        
    }
}
