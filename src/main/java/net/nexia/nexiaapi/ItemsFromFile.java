package net.nexia.nexiaapi;


import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
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
import java.util.*;

@SuppressWarnings("unused")
public class ItemsFromFile
{

    /**
     * Allows the creation of Items from a File. Check the wiki for file syntax.
     * @param filePath The path of the file.
     * @return Returns a list of Items from the specified File.
     */
    public static List<ItemStack> getItemsFromFile(File filePath)
    {
        //Items to Return
        List<ItemStack> items = new ArrayList<>();

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(filePath);

        for (String key : yaml.getKeys(false))
        {
            ConfigurationSection section = yaml.getConfigurationSection(key);

            if (section == null) return null;

            //Add item to list
            items.add(item(section));
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

        for (String key : section.getKeys(false))
        {
            //Display Name
            if (key.equalsIgnoreCase("DisplayName"))
            {
                itemMeta.setDisplayName(Processes.color(section.getString(key)));
                continue;
            }

            //Lore
            List<String> lore = new ArrayList<>();
            if (key.equalsIgnoreCase("Lore"))
            {
                lore.addAll(section.getStringList(key));
                itemMeta.setLore(lore);
                continue;
            }

            //Enchants
            if (key.equalsIgnoreCase("Enchants"))
            {
                for (Map e : section.getMapList(key))
                {
                    String enchantmentName = e.values().toArray()[0].toString();
                    NamespacedKey namespacedKey = NamespacedKey.minecraft(enchantmentName);

                    Enchantment enchant = Enchantment.getByKey(namespacedKey);

                    if (enchant != null)
                        itemMeta.addEnchant(enchant, (Integer) e.get(e.keySet().toArray()[1]), true);
                }
                continue;
            }

            //ItemFlags
            if (key.equalsIgnoreCase("ItemFlags"))
            {
                for (Map e : section.getMapList(key))
                {
                    String itemFlagName = e.values().toArray()[0].toString();

                    ItemFlag itemFlag = ItemFlag.valueOf(itemFlagName);

                    itemMeta.addItemFlags(itemFlag);
                }
                continue;
            }

            //Unbreakable
            if (key.equalsIgnoreCase("Unbreakable"))
            {
                itemMeta.setUnbreakable(section.getBoolean(key));
                continue;
            }

            //Damage
            if (key.equalsIgnoreCase("Damage"))
            {
                item.setDamage(section.getInt(key));
                continue;
            }

            //Amount
            if (key.equalsIgnoreCase("Amount"))
            {
                item.setAmount(section.getInt(key));
                continue;
            }

            //Skulls
            if (item.getType() == Material.PLAYER_HEAD)
            {
                ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

                String textureSection = section.getString("Texture");
                String playerSection = section.getString("Player");

                if (textureSection != null)
                {
                    PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), null);
                    profile.getProperties().add(new ProfileProperty("textures", textureSection));
                    skullMeta.setPlayerProfile(profile);
                }
                else if (playerSection != null)
                {
                    skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(playerSection));
                }

                skullMeta.setLore(lore);

                item.setItemMeta(skullMeta);
            }
            else
                item.setItemMeta(itemMeta);
        }

        return item;
    }

}
