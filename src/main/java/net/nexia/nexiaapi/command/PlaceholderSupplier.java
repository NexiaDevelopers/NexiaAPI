package net.nexia.nexiaapi.command;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record PlaceholderSupplier(String name, Function<CommandSender, List<String>> supplier) {

    public static List<String> getPlaceholderValues(String placeholder, Set<PlaceholderSupplier> suppliers, CommandSender sender) {

        List<String> results = new ArrayList<>();

        if (placeholder.startsWith("${") && placeholder.endsWith("}")) {
            placeholder = placeholder.substring(2, placeholder.length() - 1);
        }

        String finalPlaceholder = placeholder;
        Optional<PlaceholderSupplier> supplier = suppliers.stream().filter(s -> s.name().equalsIgnoreCase(finalPlaceholder)).findFirst();
        supplier.ifPresent(placeholderSupplier -> results.addAll(placeholderSupplier.supplier().apply(sender)));

        return results;

    }

    public static String getRegexPattern(String command, Set<PlaceholderSupplier> suppliers, CommandSender sender) {

        command = command.replaceAll("\\s?\\.\\.\\.", ".*").trim();
        Pattern placeholderPattern = Pattern.compile("\\$\\{([^}]*)}");
        Matcher matcher = placeholderPattern.matcher(command);

        while (matcher.find()) {
            Optional<PlaceholderSupplier> supplier = suppliers.stream()
                    .filter(s -> matcher.group(1).contains(s.name())).findFirst();

            if (supplier.isPresent()) {
                String or = String.format("(%s)", String.join("|", supplier.get().supplier().apply(sender)));
                command = command.replace(matcher.group(0), or);
            }
        }
        return String.format("^%s$", command);
    }

}
