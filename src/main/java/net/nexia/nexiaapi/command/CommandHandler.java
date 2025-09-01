package net.nexia.nexiaapi.command;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class CommandHandler implements TabExecutor {

    private final Set<BaseCommand> registeredCommands = new HashSet<>();
    private final Set<PlaceholderSupplier> registeredSuppliers = new HashSet<>();

    public CommandHandler(JavaPlugin plugin, String command) {
        Objects.requireNonNull(plugin.getCommand(command)).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand(command)).setTabCompleter(this);
    }

    public void addCommand(BaseCommand parts) {
        registeredCommands.add(parts);
    }

    public void addSupplier(PlaceholderSupplier placeholderSupplier) {
        registeredSuppliers.add(placeholderSupplier);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {

        // Ensure command is the correct size and matches with placeholder regex.
        List<BaseCommand> applicableCommands = registeredCommands.stream()
                .filter(cmd -> cmd.isDotted() ?
                        cmd.args.length-1 <= args.length :
                        cmd.args.length == args.length)
                .filter(cmd -> {
                    String argsCombined = String.join(" ", args);
                    return argsCombined.matches(PlaceholderSupplier.getRegexPattern(cmd.getArgsCombined(), registeredSuppliers, commandSender));
                })
                .toList();

        if (applicableCommands.isEmpty()) {
            commandSender.sendMessage(ChatColor.RED + "Invalid command!");
            return false;
        }

        for (BaseCommand cmd : applicableCommands) {
            if (cmd.canRun(commandSender)) {
                cmd.run(commandSender, args);
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {

        return registeredCommands.stream()
                .flatMap(cmd -> {
                    String[] parts = cmd.args;
                    int currentIdx = args.length - 1;

                    // Skip if too many arguments
                    if (currentIdx >= parts.length) {
                        return Stream.empty();
                    }

                    // Ensure previous parts still match.
                    for (int i = 0; i < currentIdx; i++) {
                        String part = parts[i];

                        if (isPlaceholder(part)) {
                            List<String> values = PlaceholderSupplier.getPlaceholderValues(part, registeredSuppliers, commandSender);

                            if (!values.contains(args[i])) {
                                return Stream.empty();
                            }
                        } else if (!part.equalsIgnoreCase(args[i])) {
                            return Stream.empty();
                        }
                    }

                    String next = parts[currentIdx];

                    // Skip if next part is "..."
                    if (next.equals("...")) {
                        return Stream.empty();
                    }

                    // Check command condition
                    if (!cmd.canRun(commandSender)) {
                        return Stream.empty();
                    }

                    // If next part is a placeholder, get possible placeholder values.
                    if (!isPlaceholder(next)) {
                        return Stream.of(next);
                    }
                    return PlaceholderSupplier.getPlaceholderValues(next, registeredSuppliers, commandSender).stream();
                })
                .filter(next -> next.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .distinct()
                .sorted()
                .toList();

    }

    private boolean isPlaceholder(String part) {
        return part.startsWith("${") && part.endsWith("}");
    }
}
