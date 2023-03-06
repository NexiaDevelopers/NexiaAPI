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

        for (File f : files) //Loop through files
        {
            if (!f.getName().equals(fileName + ".yml")) continue;

            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);

            for (String y : yaml.getKeys(false)) //Loop through sections (items)
            {
                ConfigurationSection section = yaml.getConfigurationSection(y);

                if (section == null) return null;

                ItemStack item = new ItemStack(Material.valueOf(section.getString("Type")));
                String texture = section.getString("texture"); //Texture for Skulls

                if (item.getType().equals(Material.PLAYER_HEAD) && texture != null) //Skulls
                {
                    SkullMeta skullMeta = (SkullMeta) item.getItemMeta();

                    GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                    profile.getProperties().put("textures", new Property("textures", ""));
                    Field field;
                    try
                    {
                        field = skullMeta.getClass().getDeclaredField("profile");
                        field.setAccessible(true);
                        field.set(skullMeta, profile);
                    }
                    catch (NoSuchFieldException | IllegalAccessException e)
                    {
                        e.printStackTrace();
                        continue;
                    }

                    //Lore
                    List<String> lore = new ArrayList<>();
                    for (String d : section.getStringList("Description"))
                        lore.add(Processes.color(d));

                    //Setting Meta
                    skullMeta.setDisplayName(Processes.color(Objects.requireNonNull(section.getString("Name")))); //Name
                    skullMeta.setLore(lore); //Lore

                    for (Map e : section.getMapList("Enchantments")) //Enchantments
                    {
                        String enchantmentName = e.values().toArray()[0].toString();
                        NamespacedKey key = NamespacedKey.fromString("minecraft:" + enchantmentName);
                        skullMeta.addEnchant(Objects.requireNonNull(Enchantment.getByKey(key)), (Integer) e.get(e.keySet().toArray()[1]), true);
                    }

                    if (section.getBoolean("HideEnchants"))
                        skullMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS); //If enabled hide enchants
                }
                else //General Items
                {
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
                        String enchantmentName = e.values().toArray()[0].toString();
                        NamespacedKey key = NamespacedKey.fromString("minecraft:" + enchantmentName);
                        itemMeta.addEnchant(Objects.requireNonNull(Enchantment.getByKey(key)), (Integer) e.get(e.keySet().toArray()[1]), true);
                    }

                    if (section.getBoolean("HideEnchants"))
                        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS); //If enabled hide enchants

                    item.setItemMeta(itemMeta);
                }

                //Add item to list
                items.add(item);
            }
        }

        return items;
    }

}
