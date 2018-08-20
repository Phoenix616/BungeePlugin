package de.themoep.bungeeplugin;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/*
 * Licensed under the Nietzsche Public License v0.6-m1
 *
 * Copyright 2017 Max Lee (https://github.com/Phoenix616/)
 *
 * Copyright, like God, is dead.  Let its corpse serve only to guard against its
 * resurrection.  You may do anything with this work that copyright law would
 * normally restrict so long as you retain the above notice(s), this license, and
 * the following misquote and disclaimer of warranty with all redistributed
 * copies, modified or verbatim.  You may also replace this license with the Open
 * Works License version 0.9.4 (available at the http://owl.apotheon.org website)
 * or with the MIT License (available at the http://copyfree.org website).
 *
 *    Copyright is dead.  Copyright remains dead, and we have killed it.  How
 *    shall we comfort ourselves, the murderers of all murderers?  What was
 *    holiest and mightiest of all that the world of censorship has yet owned has
 *    bled to death under our knives: who will wipe this blood off us?  What
 *    water is there for us to clean ourselves?  What festivals of atonement,
 *    what sacred games shall we have to invent?  Is not the greatness of this
 *    deed too great for us?  Must we ourselves not become authors simply to
 *    appear worthy of it?
 *                                     - apologies to Friedrich Wilhelm Nietzsche
 *
 * No warranty is implied by distribution under the terms of this license.
 */

public class FileConfiguration {
    protected final static ConfigurationProvider yml = ConfigurationProvider.getProvider(YamlConfiguration.class);

    private Plugin plugin;

    private final Configuration defaultCfg;
    private Configuration config;
    private File configFile;
    private String defaultFile;

    /**
     * FileConfiguration represents a configuration saved in a yml file
     * @param plugin The bungee plugin of the config
     * @param path The path to the yml file with the plugin's datafolder as the parent
     * @throws IOException If an I/O error occurred
     */
    public FileConfiguration(Plugin plugin, String path) throws IOException {
        this(plugin, new File(plugin.getDataFolder(), path));
    }

    /**
     * FileConfiguration represents a configuration saved in a yml file
     * @param plugin The bungee plugin of the config
     * @param configFile The yml file
     * @throws IOException If an I/O error occurred
     */
    public FileConfiguration(Plugin plugin, File configFile) throws IOException {
        this(plugin, configFile, configFile.getName());
    }

    /**
     * FileConfiguration represents a configuration saved in a yml file
     * @param plugin The bungee plugin of the config
     * @param configFile The yml file
     * @param defaultFile The name of the default file inside the jar
     * @throws IOException If an I/O error occurred
     */
    public FileConfiguration(Plugin plugin, File configFile, String defaultFile) throws IOException {
        this.plugin = plugin;
        this.configFile = configFile;
        this.defaultFile = defaultFile;
        InputStream stream = plugin.getResourceAsStream(defaultFile);
        if (stream != null) {
            defaultCfg = yml.load(new InputStreamReader(stream));
        } else {
            defaultCfg = new Configuration();
        }
        loadConfig();
    }

    /**
     * Load a file into this config
     * @return <tt>true</tt> if it was successfully loaded, <tt>false</tt> if not
     * @throws IOException If an I/O error occurred
     */
    public boolean loadConfig() throws IOException {
        if(configFile.exists()) {
            config = yml.load(configFile, defaultCfg);
            return true;
        } else if(configFile.getParentFile().exists() || configFile.getParentFile().mkdirs()) {
            return createDefaultConfig();
        }
        return false;
    }

    /**
     * Saves the config into the yml file on the disc
     * @return <tt>true</tt> if it was saved; <tt>false</tt> if an error occurred
     * @throws IOException If an I/O error occurred
     */
    public boolean saveConfig() {
        try {
            yml.save(config, configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save configuration to " + configFile.getAbsolutePath());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Copy the default config from the plugin jar into its path
     * @return <tt>true</tt> if it was successfully created, <tt>false</tt> if it already existed
     * @throws IOException If an I/O error occurred
     */
    public boolean createDefaultConfig() throws IOException {
        if(configFile.createNewFile()) {
            InputStream stream = plugin.getResourceAsStream(defaultFile);
            if (stream != null) {
                config = yml.load(new InputStreamReader(stream), defaultCfg);
            } else {
                config = new Configuration();
            }
            saveConfig();
            return true;
        }
        return false;
    }    
    
    /**
     * Delete the file of this config from the disc
     * @return <tt>true</tt> if it was successfully deleted; <tt>false</tt> otherwise
     */
    public boolean removeConfig() {
        return configFile.delete();
    }

    public Configuration getConfiguration() {
        return config;
    }

    public Configuration getDefaults() {
        return defaultCfg;
    }

    public boolean isSet(String path) {
        return isSet(path, false);
    }

    public boolean isSet(String path, boolean ignoreDefaults) {
        return (ignoreDefaults ? config.get(path, null) : config.get(path)) != null;
    }

    public void set(String path, Object value) {
        config.set(path, value);
    }

    public Object get(String path) {
        return config.get(path);
    }

    public Object get(String path, Object def) {
        return config.get(path, def);
    }

    public boolean getBoolean(String path) {
        return getBoolean(path, defaultCfg.getBoolean(path));
    }

    public boolean getBoolean(String path, boolean def) {
        Object value = config.get(path);
        if (value instanceof Boolean) {
            return (boolean) value;
        } else if (value instanceof String) {
            return ((String) value).equalsIgnoreCase("true");
        }
        return def;
    }

    public int getInt(String path) {
        return getInt(path, defaultCfg.getInt(path));
    }

    public int getInt(String path, int def) {
        Object value = config.get(path);
        if (value instanceof Integer) {
            return (int) value;
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException ignored) {};
        }
        return def;
    }

    public long getLong(String path) {
        return getLong(path, defaultCfg.getLong(path));
    }

    public long getLong(String path, long def) {
        Object value = config.get(path);
        if (value instanceof Long) {
            return (long) value;
        } else if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException ignored) {};
        }
        return def;
    }

    public String getString(String path) {
        return getString(path, defaultCfg.getString(path));
    }

    public String getString(String path, String def) {
        Object value = config.get(path);
        if (value instanceof String) {
            return (String) value;
        } else if (value != null) {
            return value.toString();
        }
        return def;
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    public Configuration getSection(String path) {
        return config.getSection(path);
    }

    public boolean isSection(String path) {
        return config.get(path) instanceof Configuration;
    }

    public boolean isList(String path) {
        return config.get(path) instanceof List;
    }

    public boolean isStringList(String path) {
        Object value = config.get(path);
        return value instanceof List && !((List) value).isEmpty() && ((List) value).get(0) instanceof String;
    }

    public boolean isString(String path) {
        return config.get(path) instanceof String;
    }

    public boolean isInt(String path) {
        return config.get(path) instanceof Integer;
    }

    public boolean isLong(String path) {
        return config.get(path) instanceof Long;
    }

    public boolean isDouble(String path) {
        return config.get(path) instanceof Double;
    }

    public boolean isBoolean(String path) {
        return config.get(path) instanceof Boolean;
    }
}