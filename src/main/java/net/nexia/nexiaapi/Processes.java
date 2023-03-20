package net.nexia.nexiaapi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Processes
{

    /**
     * Allows usage of Color Codes in any String.
     * @param string String to add color to. Add the Color Code at the start.
     * @return Returns the Color Coded String.
     */
    public static String color (String string) { return ChatColor.translateAlternateColorCodes('&', string); }

    /**
     * Returns a color from a String for Spigot's Color method.
     * @param color String with the color name.
     * @return Returns the color.
     */
    public static Color colorFromString (String color)
    {
        switch (color)
        {
            case "AQUA": {return Color.AQUA;}
            case "BLACK": {return Color.BLACK;}
            case "BLUE": {return Color.BLUE;}
            case "FUCHSIA": {return Color.FUCHSIA;}
            case "GRAY": {return Color.GRAY;}
            case "GREEN": {return Color.GREEN;}
            case "LIME": {return Color.LIME;}
            case "MAROON": {return Color.MAROON;}
            case "NAVY": {return Color.NAVY;}
            case "OLIVE": {return Color.OLIVE;}
            case "ORANGE": {return Color.ORANGE;}
            case "PURPLE": {return Color.PURPLE;}
            case "RED": {return Color.RED;}
            case "SILVER": {return Color.SILVER;}
            case "TEAL": {return Color.TEAL;}
            case "YELLOW": {return Color.YELLOW;}
            default: {return Color.WHITE;}
        }
    }

    /**
     * Creates ItemStacks.
     * @param name Item name.
     * @param lore Item lore.
     * @param material Item material.
     * @param quantity Item quantity.
     * @return Returns ItemStack.
     */
    public static ItemStack buildItem(String name, List<String> lore, Material material, int quantity)
    {
        ItemStack item = new ItemStack(material, quantity);
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null)
        {
            itemMeta.setDisplayName(color(name));
            lore = (lore != null) ? lore.stream().map(Processes::color).collect(Collectors.toList()) : null;
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
        }
        return item;
    }

    /**
     * Creates Player Heads ItemStacks.
     * @param name Item name.
     * @param lore Item lore.
     * @param player Head's owner.
     * @param quantity Item quantity.
     * @return Returns ItemStack.
     */
    public static ItemStack buildSkull(String name, List<String> lore, UUID player, int quantity)
    {
        ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD, quantity);
        SkullMeta skullMeta = (SkullMeta) playerSkull.getItemMeta();
        if (skullMeta != null)
        {
            skullMeta.setDisplayName(name);
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(player));
            lore = (lore != null) ? lore.stream().map(Processes::color).collect(Collectors.toList()) : null;
            skullMeta.setLore(lore);
            playerSkull.setItemMeta(skullMeta);
        }

        return playerSkull;
    }

    /**
     * Creates Potion ItemStacks.
     * @param name Item name.
     * @param lore Item lore.
     * @param effect Potion effect.
     * @param color Potion color.
     * @param quantity Item quantity.
     * @return Returns ItemStack.
     */
    public static ItemStack buildPotion(String name, List<String> lore, PotionData effect, Color color, int quantity)
    {
        ItemStack potion = new ItemStack(Material.POTION, quantity);
        ItemMeta itemMeta = potion.getItemMeta();
        if (itemMeta != null)
        {
            itemMeta.setDisplayName(color(name));
            PotionMeta potionMeta = (PotionMeta) itemMeta;
            potionMeta.setBasePotionData(effect);
            if (color != null) potionMeta.setColor(color);
            lore = (lore != null) ? lore.stream().map(Processes::color).collect(Collectors.toList()) : null;
            itemMeta.setLore(lore);
            potion.setItemMeta(itemMeta);
        }

        return potion;
    }

    /**
     * Checks if given String can be parsed to Integer.
     * @param string String To Check.
     * @return Returns True/False.
     */
    public static boolean isInteger(String string)
    {
        try
        {
            Integer.parseInt(string);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

    /**
     * Saves changes to .yml files.
     * @param file The File to save.
     * @param yamlConfiguration The YamlConfiguration of the File to save.
     */
    public static void saveFileChanges(File file, YamlConfiguration yamlConfiguration)
    {
        try
        {
            yamlConfiguration.save(file);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gives an Item to a Player.
     * If the Player's Inventory is full it will drop the Item to the Player's location.
     * @param player The Player to give the Item to.
     * @param item The Item to give to the Player.
     */
    public static void giveToPlayer(Player player, ItemStack item)
    {
        if (player.getInventory().firstEmpty() == -1)
            player.getWorld().dropItem(player.getLocation(), item);
        else
            player.getInventory().addItem(item);
    }

}