package de.themoep.bungeeplugin;/*
 * bungeeplugin
 * Copyright (C) 2023. Max Lee aka Phoenix616 (mail@moep.tv)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


import io.github.waterfallmc.waterfall.event.ProxyDefineCommandsEvent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class WaterfallListener implements Listener {

    @EventHandler
    public void onCommandsDefine(ProxyDefineCommandsEvent event) {
        // Remove commands that the player doesn't have access to
        if (event.getSender() instanceof CommandSender sender)
            event.getCommands().entrySet().removeIf(
                    e -> e.getValue() instanceof PluginCommand pluginCommand && !pluginCommand.hasCommandPermission(sender));
    }
}
