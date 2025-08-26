package net.nexia.nexiaapi.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class PlayerCommand extends BaseCommand {

    protected String permission = null;

    public PlayerCommand(String command) {
        super(command);
    }

    public PlayerCommand(String command, BiConsumer<CommandSender, String[]> function) {
        super(command, function);
    }

    public PlayerCommand(String command, String permission, BiConsumer<CommandSender, String[]> function) {
        super(command, function);
        this.permission = permission;
    }

    public void setPermission(@Nullable String permission) {
        this.permission = permission;
    }

    @Override
    public boolean canRun(CommandSender sender) {

        if (!(sender instanceof Player)) {
            return false;
        }
        if (permission != null && !sender.hasPermission(permission)) {
            return false;
        }
        return condition.test(sender);
    }

}
