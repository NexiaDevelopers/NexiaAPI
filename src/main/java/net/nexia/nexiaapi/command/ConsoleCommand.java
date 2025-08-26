package net.nexia.nexiaapi.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public class ConsoleCommand extends BaseCommand {

    public ConsoleCommand(String command) {
        super(command);
    }

    public ConsoleCommand(String command, BiConsumer<CommandSender, String[]> function) {
        super(command, function);
    }

    @Override
    public boolean canRun(CommandSender sender) {

        if (sender instanceof Player) {
            return false;
        }
        return condition.test(sender);
    }

}
