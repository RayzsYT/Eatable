package de.rayzs.eatable.utils.configuration;

import org.bukkit.configuration.file.YamlConfiguration;
import de.rayzs.eatable.utils.Logger;
import org.bukkit.ChatColor;
import java.util.Collection;
import java.io.File;

public class ConfigurationBuilder {

    private String fileName, filePath;
    private File file;
    private boolean loadDefault;
    private YamlConfiguration configuration;

    public ConfigurationBuilder(String fileName) {
        init(fileName);
    }

    public ConfigurationBuilder(String fileName, String filePath) {
        this.filePath = filePath;
        init(fileName);
    }

    protected void init(String fileName) {
        this.fileName = fileName;
        this.file = new File((this.filePath == null) ? "./plugins/Eatable" : this.filePath, fileName + ".yml");
        this.loadDefault = !this.file.exists();
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    public String getFilePath() {
        return filePath;
    }

    public void reload() {
        init(this.fileName);
    }

    public void save() {
        try { this.configuration.save(this.file);
        } catch (Exception exception) {
            Logger.info("Could not save configuration file! [file=" + this.fileName + ", message=" + exception.getMessage() + "]");
        }
    }

    public ConfigurationBuilder set(String path, String target, Object object) {
        this.configuration.set(((path != null) ? (path + ".") : "") + target, object instanceof String ? ((String) object).replace("§", "&") : object);
        return this;
    }

    public ConfigurationBuilder set(String target, Object object) {
        set(null, target, object);
        return this;
    }

    public ConfigurationBuilder setAndSave(String path, String target, Object object) {
        set(path, target, object);
        save();
        return this;
    }

    public ConfigurationBuilder setAndSave(String target, Object object) {
        set(target, object);
        save();
        return this;
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public Object getOrSet(String path, String target, Object object) {
        Object result = get(path, target);
        if (result != null)
            return result;

        set(path, target, object);
        save();

        return get(path, target);
    }

    public Object getOrSet(String target, Object object) {
        Object result = get(target);
        if (result != null)
            return result;

        set(target, object);
        save();

        return get(target);
    }

    public Object get(String target) {
        return get(null, target);
    }

    public Object get(String path, String target) {
        Object object = this.configuration.get(((path != null) ? (path + ".") : "") + target);
        if (object instanceof String) {
            String objString = (String) object;
            return ChatColor.translateAlternateColorCodes('&', objString);
        }
        return object;
    }

    public Collection<String> getKeys(boolean deep) {
        return this.configuration.getKeys(deep);
    }

    public Collection<String> getKeys(String section, boolean deep) {
        return this.configuration.getConfigurationSection(section).getKeys(deep);
    }

    public File getFile() {
        return file;
    }

    public boolean loadDefault() {
        return loadDefault;
    }
}