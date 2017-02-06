package de.themoep.bungeeplugin;

/*
 * Licensed under the Nietzsche Public License v0.6
 *
 * Copyright 2017 Max Lee (https://github.com/Phoenix616/)
 *
 * Copyright, like God, is dead.  Let its corpse serve only to guard against its
 * resurrection.  You may do anything with this work that copyright law would
 * normally restrict so long as you retain the above notice(s), this license, and
 * the following misquote and disclaimer of warranty with all redistributed
 * copies, modified or verbatim.  You may also replace this license with the Open
 * Works License, available at the http://owl.apotheon.org website.
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

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;

public abstract class BungeePlugin extends Plugin {

    private boolean enabled;
    private FileConfiguration descConfig;
    private FileConfiguration pluginConfig;

    @Override
    public void onLoad() {
        try {
            descConfig = new FileConfiguration(this, getDescription().getFile());
            pluginConfig = new FileConfiguration(this, "config.yml");
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Error while loading plugin. Will not enable!", e);
            enabled = false;
        }
        enabled = true;
    }

    protected void registerCommand(String name, Class<? extends PluginCommand> commandClass) {
        Constructor<? extends PluginCommand> simpleConstructor = null;
        Constructor<? extends PluginCommand> extendedConstructor = null;
        try {
            simpleConstructor = commandClass.getConstructor(BungeePlugin.class, String.class);
            extendedConstructor = commandClass.getConstructor(BungeePlugin.class, String.class, String.class, String.class, String.class, String.class, String[].class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        Configuration commandSection = descConfig.getSection("commands." + name);
        PluginCommand command = null;
        try {
            if (extendedConstructor != null) {
                if (!commandSection.getKeys().isEmpty()) {
                    List<String> aliases = commandSection.getStringList("aliases");
                    command = extendedConstructor.newInstance(
                            this,
                            name,
                            commandSection.getString("permission"),
                            commandSection.getString("permission-message"),
                            commandSection.getString("description"),
                            commandSection.getString("usage"),
                            aliases != null ? aliases.toArray(new String[aliases.size()]) : new String[0]
                    );
                } else {
                    command = extendedConstructor.newInstance(this, name, null);
                }
            } else if (simpleConstructor != null) {
                command = simpleConstructor.newInstance(this, name);
            } else {
                getLogger().log(Level.SEVERE, "Could not find any constructors in the command class " + commandClass + "! Disabling plugin!");
                enabled = false;
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            getLogger().log(Level.SEVERE, "Error while registering command "  + name + "! Disabling plugin!", e);
            enabled = false;
        }

        if (command != null) {
            getProxy().getPluginManager().registerCommand(this, command);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public FileConfiguration getConfig() {
        return pluginConfig;
    }

    public String translate(String message, String... replacements) {
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            message = message.replace("%" + replacements[i] + "%", replacements[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
