package net.nexia.nexiaapi.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public abstract class BaseCommand {

    protected final String[] args;
    protected BiConsumer<CommandSender, String[]> function;
    protected Predicate<CommandSender> condition = (sender) -> true;

    public BaseCommand(String args) {
        this.args = !args.isBlank() ? args.split(" ") : new String[0];
    }

    public BaseCommand(String args, BiConsumer<CommandSender, String[]> function) {
        this.args = args.split(" ");
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

    public String getArgsCombined() {
        return String.join(" ", args);
    }

    public boolean isDotted() {
        return args.length > 0 && args[args.length - 1].equals("...");
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
