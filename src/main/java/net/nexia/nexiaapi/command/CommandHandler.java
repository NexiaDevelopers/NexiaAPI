package net.nexia.nexiaapi.command;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class CommandHandler implements TabExecutor {

    private final JavaPlugin plugin;
    private final Set<BaseCommand> registeredCommands = new HashSet<>();
    private final Set<PlaceholderSupplier> registeredSuppliers = new HashSet<>();

    public CommandHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerCommand(BaseCommand command) {

        String[] commandParts = command.fullCommand.split(" ");

        if (commandParts.length == 0) {
            plugin.getLogger().severe("Command name cannot be empty!");
            return;
        }

        if (registeredCommands.stream().noneMatch(cmd -> cmd.fullCommand.startsWith(commandParts[0]))) {
            PluginCommand pluginCommand = plugin.getCommand(commandParts[0]);

            if (pluginCommand == null) return;

            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);
        }

        registeredCommands.add(command);
    }

    public void registerSupplier(PlaceholderSupplier placeholderSupplier) {
        registeredSuppliers.add(placeholderSupplier);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {

        String fullCommand = String.format("%s %s", label, String.join(" ", args)).trim();

        // Ensure command is the correct size and matches with placeholder regex.
        List<BaseCommand> applicableCommands = registeredCommands.stream()
                .filter(cmd -> cmd.getArgs().length == args.length)
                .filter(cmd -> fullCommand.matches(PlaceholderSupplier.getRegexPattern(cmd.fullCommand, registeredSuppliers, commandSender)))
                .toList();

        applicableCommands.forEach(cmd -> cmd.run(commandSender, args));

        if (applicableCommands.isEmpty()) {
            commandSender.sendMessage(ChatColor.RED + "Invalid command!");
            return false;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {

        return registeredCommands.stream()
                .flatMap(cmd -> {
                    String[] parts = cmd.getArgs();
                    int currentIdx = args.length - 1;

                    // Skip if too many arguments
                    if (currentIdx >= parts.length) return Stream.empty();

                    // Ensure previous parts still match. Placeholders can match anything.
                    for (int i = 0; i < currentIdx; i++) {
                        String part = parts[i];
                        if (!(part.startsWith("${") && part.endsWith("}"))) {
                            if (!part.equalsIgnoreCase(args[i])) return Stream.empty();
                        }
                    }

                    String next = parts[currentIdx];

                    // If next part is a placeholder, get possible placeholder values.
                    if (next.startsWith("${") && next.endsWith("}")) {
                        return PlaceholderSupplier.getPlaceholderValues(next, registeredSuppliers, commandSender).stream();
                    }
                    return Stream.of(next);
                })
                .filter(next -> next.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .distinct()
                .sorted()
                .toList();

    }
}
