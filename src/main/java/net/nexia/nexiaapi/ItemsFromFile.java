package net.nexia.nexiaapi;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.j2objc.annotations.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.*;

@SuppressWarnings("unused")
public class ItemsFromFile
{

    /**
     * Allows the creation of Items from a File. Check the wiki for file syntax.
     * @param fileName The name of the file. Do not include file extension.
     * @param filePath The path of the file.
     * @return Returns a list of Items from the specified File.
     */
    public static List<ItemStack> getItemsFromFile(String fileName, File filePath)
    {
        //Items to Return
        List<ItemStack> items = new ArrayList<>();

        File[] files = filePath.listFiles(); //Rewards Files

        if (files == null) return null;

        for (File f : files)
        {
            if (!f.getName().equals(fileName + ".yml")) continue;

            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);

            for (String y : yaml.getKeys(false))
            {
                ConfigurationSection section = yaml.getConfigurationSection(y);

                if (section == null) return null;

                //Add item to list
                items.add(item(section));
            }
        }

        return items;
    }

    /**
     * Allows the creation of an Item from a Configuration Section.
     * @param section The Configuration Section to get the Item from.
     * @return Returns the Item from the specified Section.
     */
    public static ItemStack getItemFromSection(ConfigurationSection section)
    {
        return item(section);
    }

    private static ItemStack item(ConfigurationSection section)
    {
        ItemStack item = new ItemStack(Material.valueOf(section.getString("Type")));

        //Item
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return null;

        //Lore
        List<String> lore = new ArrayList<>();
        for (String d : section.getStringList("Description"))
            lore.add(Processes.color(d));
        itemMeta.setLore(lore); //Lore

        //Setting Meta
        if (section.getString("Name") != null)
            itemMeta.setDisplayName(Processes.color(section.getString("Name"))); //Name

        for (Map e : section.getMapList("Enchantments")) //Enchantments
        {
            String enchantmentName =  e.values().toArray()[0].toString();
            NamespacedKey key = NamespacedKey.minecraft(enchantmentName);
            itemMeta.addEnchant(Objects.requireNonNull(Enchantment.getByKey(key)), (Integer) e.get(e.keySet().toArray()[1]), true);
        }

        if (section.getBoolean("HideEnchantments"))
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS); //If enabled hide enchants

        item.setItemMeta(itemMeta);

        return item;
    }

    public static void jitpackTest(Player player)
    {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), null);
        profile.getProperties().add(new ProfileProperty("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmJjMzE3YzQxODQ0NjZhNDk2MDYxMWZjZjlhZmVhMDk1NGNjMjgyZTNiMzJjMjYxNWI5Y2I3NDc2NGY0NzM1YiJ9fX0="));
        skullMeta.setPlayerProfile(profile);
        skull.setItemMeta(skullMeta);

        player.getInventory().addItem(skull);
    }

}
