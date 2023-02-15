package net.nexia.nexiaapi;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings("unused")
public class ItemsFromFile
{

    /**
     * Allows the creation of items from files. Check the wiki for file syntax.
     * @param fileName The name of the file. Do not include file extension.
     * @param filePath The path of the file.
     * @return Returns a list of items from the specified file.
     */
    public static List<ItemStack> GetItems(String fileName, File filePath)
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

                //General Items
                ItemStack item = new ItemStack(Material.valueOf(section.getString("Type")));
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

                //Skulls
                if (item.getType() == Material.PLAYER_HEAD)
                {
                    SkullMeta itemMetaSkull = (SkullMeta) item.getItemMeta();

                    GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                    profile.getProperties().put("textures", new Property("textures", section.getString("Texture")));
                    Field field;
                    try
                    {
                        field = itemMetaSkull.getClass().getDeclaredField("profile");
                        field.setAccessible(true);
                        field.set(itemMetaSkull, profile);
                    }
                    catch (NoSuchFieldException | IllegalAccessException e)
                    {
                        e.printStackTrace();
                        return null;
                    }

                    item.setItemMeta(itemMetaSkull);
                }

                items.add(item); //Add item to list
            }
        }

        return items;
    }

}
