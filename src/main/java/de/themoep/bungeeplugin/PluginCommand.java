package de.themoep.bungeeplugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

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

public abstract class PluginCommand extends Command {

    protected final BungeePlugin plugin;

    private final String permission;
    private final String permissionMessage;
    private final String description;
    private final String usage;

    public PluginCommand(BungeePlugin plugin, String name) {
        this(plugin, name, null, null, null, null);
    }

    public PluginCommand(BungeePlugin plugin, String name, String permission, String permissionMessage, String description, String usage, String... aliases) {
        super(name, null, aliases);
        this.plugin = plugin;
        this.permission = permission;
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

    public BungeePlugin getPlugin() {
        return plugin;
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
