package net.nexia.nexiaapi;

import net.nexia.nexiaapi.command.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class NexiaAPI extends JavaPlugin 
{
    @Override
    public void onEnable() {

        CommandHandler commandHandler = new CommandHandler(this);
        commandHandler.registerCommand(new SimpleCommand("guppy", (sender, args) -> getLogger().info("guppy")));
        commandHandler.registerCommand(new SimpleCommand("guppy ${abc}", (sender, args) -> getLogger().info("guppy " + args[0] )));
        commandHandler.registerCommand(new SimpleCommand("guppy players ${players}", (sender, args) -> getLogger().info("guppy player " + args[0] )));
        commandHandler.registerCommand(new SimpleCommand("guppy players ${players} ${players} ${abc}", (sender, args) -> sender.sendMessage("guppy players " + String.join(" ", args))));
        commandHandler.registerCommand(new SimpleCommand("guppy players ${players} ${abc}", (sender, args) -> sender.sendMessage("guppy players " + String.join(" ", args))));
        commandHandler.registerCommand(new PlayerCommand("guppy player", (sender, args) -> sender.sendMessage("player worked")));
        commandHandler.registerCommand(new PlayerCommand("guppy playernou ${playernou}", (sender, args) -> sender.sendMessage("player worked")));
        commandHandler.registerCommand(new ConsoleCommand("guppy console", (sender, args) -> sender.sendMessage("console worked")));

        commandHandler.registerSupplier(new PlaceholderSupplier("abc", (sender) -> List.of("aaa", "bbb", "ccc")));
        commandHandler.registerSupplier(new PlaceholderSupplier("players", (sender) -> Bukkit.getOnlinePlayers().stream().map(Player::getDisplayName).toList()));
        commandHandler.registerSupplier(new PlaceholderSupplier("playernou", (sender) -> Bukkit.getOnlinePlayers().stream().filter(p -> !p.equals(sender)).map(Player::getDisplayName).toList()));

    }

    @Override
    public void onDisable() {
        
    }
}
