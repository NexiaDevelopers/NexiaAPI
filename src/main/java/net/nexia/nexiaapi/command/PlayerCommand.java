package net.nexia.nexiaapi.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public class PlayerCommand extends BaseCommand {

    protected String permission = "";

    public PlayerCommand(String command, BiConsumer<CommandSender, String[]> function) {
        super(command, function);
    }

    public PlayerCommand(String command, String permission, BiConsumer<CommandSender, String[]> function) {
        super(command, function);
        this.permission = permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    @Override
    public void run(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This is a player command.");
            return;
        }

        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "You're not allowed to run this command.");
            return;
        }

        if (!condition.test(sender)) {
            return;
        }

        function.accept(sender, args);

    }

}
