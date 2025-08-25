package net.nexia.nexiaapi.command;

import org.bukkit.command.CommandSender;

import java.util.function.BiConsumer;

public class SimpleCommand extends BaseCommand {

    public SimpleCommand(String command, BiConsumer<CommandSender, String[]> function) {
        super(command, function);
    }

    @Override
    public void run(CommandSender sender, String[] args) {

        if (!condition.test(sender)) {
            return;
        }

        function.accept(sender, args);

    }
}
