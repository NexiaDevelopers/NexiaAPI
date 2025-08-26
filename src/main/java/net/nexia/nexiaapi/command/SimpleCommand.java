package net.nexia.nexiaapi.command;

import org.bukkit.command.CommandSender;

import java.util.function.BiConsumer;

public class SimpleCommand extends BaseCommand {

    public SimpleCommand(String command) {
        super(command);
    }

    public SimpleCommand(String command, BiConsumer<CommandSender, String[]> function) {
        super(command, function);
    }

    @Override
    public boolean canRun(CommandSender sender) {
        return condition.test(sender);
    }

}
