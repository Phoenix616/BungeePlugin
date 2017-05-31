package de.themoep.bungeeplugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.config.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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

public abstract class PluginCommand extends Command implements TabExecutor {

    protected final BungeePlugin plugin;

    private final String permission;
    private final String permissionMessage;
    private final String description;
    private final String usage;
    private final String[] aliases;

    public PluginCommand(BungeePlugin plugin, String name) {
        super(name, null);
        this.plugin = plugin;
        Configuration commandSection = plugin.getDescriptionConfig().getSection("commands." + name);
        if (!commandSection.getKeys().isEmpty()) {
            this.permission = commandSection.getString("permission");

            String permissionMessage = commandSection.getString("permission-message");
            this.permissionMessage = permissionMessage != null ? ChatColor.translateAlternateColorCodes('&', permissionMessage) : "";

            String description = commandSection.getString("description");
            this.description = description != null ? ChatColor.translateAlternateColorCodes('&', description) : "";

            String usage = commandSection.getString("usage");
            this.usage = usage != null ? ChatColor.translateAlternateColorCodes('&', usage) : "/<command>";

            List<String> aliases = commandSection.getStringList("aliases");
            this.aliases = aliases != null ? aliases.toArray(new String[aliases.size()]) : new String[0];
        } else {
            permission = null;
            permissionMessage = "";
            description = "";
            usage = "/<command>";
            aliases = new String[0];
        }
    }

    public PluginCommand(BungeePlugin plugin, String name, String permission, String permissionMessage, String description, String usage, String... aliases) {
        super(name, null);
        this.plugin = plugin;
        this.permission = permission;
        this.aliases = aliases;
        this.permissionMessage = permissionMessage != null ? ChatColor.translateAlternateColorCodes('&', permissionMessage) : "";
        this.description = description != null ? ChatColor.translateAlternateColorCodes('&', description) : "";
        this.usage = usage != null ? ChatColor.translateAlternateColorCodes('&', usage) : "/<command>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (getCommandPermission() != null && !sender.hasPermission(getCommandPermission())) {
            if (!getPermissionMessage().isEmpty()) {
                sender.sendMessage(ChatColor.RED + getPermissionMessage().replace("<permission>", getCommandPermission()));
            } else {
                sender.sendMessage(plugin.getProxy().getTranslation("no_permission"));
            }
            return;
        }

        if (!run(sender, args) && !getUsage().isEmpty()) {
            sender.sendMessage(ChatColor.RED + getUsage().replace("<command>", getName()));
        }
    }

    protected abstract boolean run(CommandSender sender, String[] args);

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> tabList = new ArrayList<>();
        for (ProxiedPlayer p : plugin.getProxy().getPlayers())
            if (args.length == 0 || p.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                tabList.add(p.getName());
        return tabList;
    }

    public BungeePlugin getPlugin() {
        return plugin;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getCommandPermission() {
        return permission;
    }

    public String getPermissionMessage() {
        return permissionMessage;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

}
