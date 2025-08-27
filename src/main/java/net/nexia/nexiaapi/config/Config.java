package net.nexia.nexiaapi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getLogger;

public class Config {

    private JavaPlugin plugin;
    private YamlConfiguration data;
    private String path;
    private File file;

    public Config(JavaPlugin plugin, String path, boolean newFile) {
        this.plugin = plugin;
        this.path = path;
        file = new File(plugin.getDataFolder(), path);

        if (!file.exists()) {
            if (newFile) {
                try {
                    file.mkdirs();
                    file.delete();
                    file.createNewFile();
                } catch (IOException e) {
                    getLogger().severe(String.format("Failed to create file '%s'%n%s", getFileName(), e));
                }
            } else {
                plugin.saveResource(path, false);
            }
        }
        reload();
    }

    public Config(JavaPlugin plugin, String path) {
        this(plugin, path, false);
    }

    public <T> HashMap<String, T> getHashMap(String path, Class<T> type) {
        Object obj = data.get(path);
        if (obj instanceof MemorySection memorySection) {
            HashMap<String, T> hashMap = new HashMap<>();
            memorySection.getKeys(false).forEach(key -> {
                @SuppressWarnings("unchecked")
                T value = (T) memorySection.get(key);
                hashMap.put(key, value);
            });
            return hashMap;
        }
        return new HashMap<>();
    }

    public HashMap<String, Object> getHashMap(String path) {
        return getHashMap(path, Object.class);
    }

    public YamlConfiguration getData() {
        return data;
    }

    public boolean write(String path, Object value, boolean saveAfter) {
        if (!fileExists()) {
            return false;
        }

        data.set(path, value);

        if (Boolean.TRUE.equals(saveAfter)) {
            save();
        }

        return true;
    }

    public boolean write(String path, Object value) {
        return write(path, value, true);
    }

    public void reload() {
        if (data == null) {
            data = YamlConfiguration.loadConfiguration(file);
        }
        InputStream stream = plugin.getResource(path);

        if (stream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
            data.setDefaults(defaultConfig);
            data.options().copyDefaults(true);
        }
        save();
    }

    public void save() {
        if (!fileExists()) {
            return;
        }

        try {
            data.save(file);
        } catch (IOException e) {
            getLogger().severe(String.format("Failed to save file '%s'%n%s", getFileName(), e));
        }
    }

    private String getFileName() {
        return file == null ? path : file.getName();
    }

    private boolean fileExists() {
        if (file == null || !file.exists()) {
            getLogger().severe(String.format("File '%s' doesn't exist", getFileName()));
            return false;
        }
        return true;
    }
}