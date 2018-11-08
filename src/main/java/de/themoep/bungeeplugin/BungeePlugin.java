package de.themoep.bungeeplugin;

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

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.io.IOException;
import java.util.logging.Level;

public abstract class BungeePlugin extends Plugin {

    private boolean enabled;
    private FileConfiguration descConfig;
    private FileConfiguration pluginConfig;

    @Override
    public void onLoad() {
        try {
            descConfig = new FileConfiguration(this, getResourceAsStream("bungee.yml") != null ? "bungee.yml" : "plugin.yml");
            removeFromConfig(descConfig.getConfiguration(), "name", "main", "version", "author", "depends", "softdepends", "description");
            descConfig.saveConfig();
            pluginConfig = new FileConfiguration(this, getResourceAsStream("bungee-config.yml") != null ? "bungee-config.yml" : "config.yml");
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Error while loading plugin. Will not enable!", e);
            enabled = false;
        }
        enabled = true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public FileConfiguration getConfig() {
        return pluginConfig;
    }

    public FileConfiguration getDescriptionConfig() {
        return descConfig;
    }

    /**
     * Add replacements (%var%) to a message and translate colorcodes
     * @param message       The message
     * @param replacements  Replacements
     */
    public static String translate(String message, String... replacements) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            message = message.replace("%" + replacements[i] + "%", replacements[i + 1]);
        }
        return message;
    }

    /**
     * Broadcast a message to all players with a certain permission
     * @param permission    The permission that the players need
     * @param message       The message to broadcast
     * @param replacements  Replacements
     */
    public static void broadcast(String permission, String message, String... replacements) {
        message = translate(message, replacements);
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (permission == null || player.hasPermission(permission)) {
                player.sendMessage(message);
            }
        }
    }

    /**
     * Remove certain paths from a config
     * @param config    The config to remove the paths from
     * @param paths     All the paths to remove
     */
    public static void removeFromConfig(Configuration config, String... paths) {
        for (String path : paths) {
            config.set(path, null);
        }
    }
}
