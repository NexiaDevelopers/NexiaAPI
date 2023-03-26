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
        ItemStack item = new ItemStack(Material.AIR);

        for (String key : section.getKeys(false))
        {
            //Type
            if (key.equalsIgnoreCase("Type"))
                item.setType(Material.valueOf(section.getString(key)));

            ItemMeta itemMeta = item.getItemMeta();

            //Display Name
            if (key.equalsIgnoreCase("DisplayName"))
                itemMeta.setDisplayName(Processes.color(section.getString(key)));

            //Amount
            if (key.equalsIgnoreCase("Amount"))
                item.setAmount(section.getInt(key));

            //Lore
            List<String> lore = new ArrayList<>();
            if (key.equalsIgnoreCase("Lore"))
            {
                for (String l : section.getStringList(key))
                    lore.add(Processes.color(l));
                itemMeta.setLore(lore);
            }

            //Enchants
            if (key.equalsIgnoreCase("Enchants"))
            {
                for (Map e : section.getMapList(key))
                {
                    String enchantmentName = e.keySet().toArray()[0].toString().toLowerCase();
                    NamespacedKey namespacedKey = NamespacedKey.minecraft(enchantmentName);
                    Enchantment enchant = Enchantment.getByKey(namespacedKey);

                    if (enchant != null)
                        itemMeta.addEnchant(enchant, (Integer) e.get(e.keySet().toArray()[0]), true);
                }
            }

            //ItemFlags
            if (key.equalsIgnoreCase("ItemFlags"))
            {
                for (Object e : Objects.requireNonNull(section.getList(key)))
                {
                    String itemFlagName = e.toString();
                    ItemFlag itemFlag = ItemFlag.valueOf(itemFlagName);

                    itemMeta.addItemFlags(itemFlag);
                }
            }

            //Unbreakable
            if (key.equalsIgnoreCase("Unbreakable"))
                itemMeta.setUnbreakable(section.getBoolean(key));

            //Skulls
            if (item.getType() == Material.PLAYER_HEAD)
            {
                SkullMeta skullMeta = (SkullMeta) itemMeta;

                if (key.equalsIgnoreCase("Texture")) //From Texture
                {
                    PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), null);
                    profile.getProperties().add(new ProfileProperty("textures", Objects.requireNonNull(section.getString(key))));
                    skullMeta.setPlayerProfile(profile);
                }

                if (key.equalsIgnoreCase("Player")) //From Player
                    skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(Objects.requireNonNull(section.getString(key))));

                item.setItemMeta(skullMeta);
            }
            else
                item.setItemMeta(itemMeta);
        }

        return item;
    }

}
