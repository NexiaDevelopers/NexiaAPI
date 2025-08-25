package net.nexia.nexiaapi.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public class ConsoleCommand extends BaseCommand {

    public ConsoleCommand(String command, BiConsumer<CommandSender, String[]> function) {
        super(command, function);
    }

    @Override
    public void run(CommandSender sender, String[] args) {

        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.RED + "This is a console command.");
            return;
        }

        if (!condition.test(sender)) {
            return;
        }

        function.accept(sender, args);

    }
}
