package net.nexia.nexiaapi.command;

import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public abstract class BaseCommand {

    protected final String fullCommand;
    protected BiConsumer<CommandSender, String[]> function;
    protected Predicate<CommandSender> condition = (sender) -> true;

    public BaseCommand(String command, BiConsumer<CommandSender, String[]> function) {
        this.fullCommand = command;
        this.function = function;
    }

    public void setCondition(Predicate<CommandSender> condition) {
        this.condition = condition;
    }

    public String[] getArgs() {
        String[] parts = fullCommand.split(" ");
        if (parts.length <= 1) return new String[0];
        return Arrays.copyOfRange(parts, 1, parts.length);
    }

    public abstract void run(CommandSender sender, String[] args);

}
