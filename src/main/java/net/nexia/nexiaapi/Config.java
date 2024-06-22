package net.nexia.nexiaapi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

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
					plugin.getLogger().severe(String.format("Failed to create file '%s'%n%s", getFileName(), e));
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

	public Object read(String path, Object defaultValue) {
		if (!fileExists()) {
			return defaultValue;
		}

		Object obj = data.get(path);
		if (obj == null) {
			return defaultValue;
		}
		return obj;
	}

	public Object read(String path) {
		return read(path, new Object());
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
			plugin.getLogger().severe(String.format("Failed to save file '%s'%n%s", getFileName(), e));
		}
	}

	private String getFileName() {
		return file == null ? path : file.getName();
	}

	private boolean fileExists() {
		if (file == null || !file.exists()) {
			plugin.getLogger().severe(String.format("File '%s' doesn't exist", getFileName()));
			return false;
		}
		return true;
	}
}
