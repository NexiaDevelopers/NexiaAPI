package net.nexia.nexiaapi.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public abstract class BaseCommand {

    protected final String fullCommand;
    protected BiConsumer<CommandSender, String[]> function;
    protected Predicate<CommandSender> condition = (sender) -> true;

    public BaseCommand(String command) {
        this.fullCommand = command;
    }

    public BaseCommand(String command, BiConsumer<CommandSender, String[]> function) {
        this.fullCommand = command;
        this.function = function;
    }

    public BaseCommand setFunction(BiConsumer<CommandSender, String[]> function) {
        this.function = function;
        return this;
    }

    public BaseCommand setCondition(Predicate<CommandSender> condition) {
        this.condition = condition;
        return this;
    }

    public String[] getArgs() {
        String[] parts = fullCommand.split(" ");
        if (parts.length <= 1) return new String[0];
        return Arrays.copyOfRange(parts, 1, parts.length);
    }

    public abstract boolean canRun(CommandSender sender);

    public void run(CommandSender sender, String[] args) {

        if (function == null) {
            sender.sendMessage(ChatColor.RED + "This command has no function.");
            return;
        }
        function.accept(sender, args);

    }

}
