package net.nexia.nexiaapi;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

        //Setting Meta
        itemMeta.setDisplayName(Processes.color(Objects.requireNonNull(section.getString("Name")))); //Name
        itemMeta.setLore(lore); //Lore

        for (Map e : section.getMapList("Enchantments")) //Enchantments
        {
            String enchantmentName =  e.values().toArray()[0].toString();
            NamespacedKey key = NamespacedKey.fromString("minecraft:" + enchantmentName);
            itemMeta.addEnchant(Objects.requireNonNull(Enchantment.getByKey(key)), (Integer) e.get(e.keySet().toArray()[1]), true);
        }

        if (section.getBoolean("HideEnchants")) itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS); //If enabled hide enchants

        item.setItemMeta(itemMeta);

        return item;
    }

}
