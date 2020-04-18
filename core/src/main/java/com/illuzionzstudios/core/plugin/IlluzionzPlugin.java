package com.illuzionzstudios.core.plugin;

import com.illuzionzstudios.config.Config;
import com.illuzionzstudios.core.locale.Locale;
import com.illuzionzstudios.core.util.Logger;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */

/**
 * All plugins with this library extend here to create the base plugin
 */
public abstract class IlluzionzPlugin extends JavaPlugin {

    /**
     * If the plugin is in DEBUG mode
     */
    public static final boolean DEBUG = true;

    /**
     * Instance of our plugin
     */
    protected static IlluzionzPlugin INSTANCE;

    /**
     * Locale for this plugin.
     */
    protected Locale locale;

    /**
     * Core plugin config
     */
    protected Config config = new Config(this);

    /**
     * Main console sender
     */
    protected ConsoleCommandSender console = Bukkit.getConsoleSender();

    /**
     * Something fatal occurred, stop the plugin
     */
    private boolean emergencyStop = false;

    /**
     * Get instance of a plugin, to be overriden
     */
    public static IlluzionzPlugin getInstance() {
        return INSTANCE;
    }

    @Override
    public final void onLoad() {
        try {
            onPluginLoad();
            INSTANCE = this;
        } catch (Throwable t) {
            Logger.severe(
                    "Unexpected error while loading " + getDescription().getName()
                            + " v" + getDescription().getVersion()
                            + ": Disabling plugin!", t);
            emergencyStop = true;
        }
    }

    @Override
    public final void onEnable() {
        if (emergencyStop) {
            setEnabled(false);
            return;
        }

        console.sendMessage(" "); // blank line to separate chatter
        console.sendMessage(ChatColor.GREEN + "=============================");
        console.sendMessage(String.format("%s%s %s by %sIlluzionz Studios", ChatColor.GRAY.toString(),
                getPluginName(), getPluginVersion(), ChatColor.BLUE.toString()));
        console.sendMessage(String.format("%sAction: %s%s%s...", ChatColor.GRAY.toString(),
                ChatColor.GREEN.toString(), "Enabling", ChatColor.GRAY.toString()));

        try {
            // plugin setup
            locale = Locale.loadDefaultLocale(this, "en_US");

            onPluginEnable();
            if (emergencyStop) {
                console.sendMessage(ChatColor.RED + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                console.sendMessage(" ");
                return;
            }
        } catch (Throwable t) {
            Logger.severe(
                    "Unexpected error while loading " + getPluginName()
                            + " v" + getPluginVersion()
                            + ": Disabling plugin!", t);
            emergencyStop();
            console.sendMessage(ChatColor.RED + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            console.sendMessage(" ");
            if (DEBUG) t.printStackTrace();
            return;
        }

        console.sendMessage(ChatColor.GREEN + "=============================");
        console.sendMessage(" "); // blank line to separate chatter
    }

    @Override
    public final void onDisable() {
        onPluginDisable();
    }

    /**
     * Called when is loaded
     */
    public abstract void onPluginLoad();

    /**
     * Called when the plugin enables
     */
    public abstract void onPluginEnable();

    /**
     * Called when the plugin disables
     */
    public abstract void onPluginDisable();

    /**
     * Called after reloadConfig​() is called
     */
    public abstract void onConfigReload();

    /**
     * Any other plugin configuration files used by the plugin.
     *
     * @return a list of Configs that are used in addition to the main config.
     */
    public abstract List<Config> getExtraConfig();

    /**
     * @return The {@link FileConfiguration} of the core config
     */
    @NotNull
    @Override
    public FileConfiguration getConfig() {
        return config.getFileConfig();
    }

    /**
     * @return Main plugin config
     */
    public Config getCoreConfig() {
        return config;
    }

    /**
     * Reload our core config and reload other stuff
     */
    @Override
    public void reloadConfig() {
        config.load();
        onConfigReload();
    }

    @Override
    public void saveConfig() {
        config.save();
    }

    public Locale getLocale() {
        return locale;
    }

    /**
     * Set the plugin's locale to a specific language
     *
     * @param localeName locale to use, eg "en_US"
     * @param reload     optionally reload the loaded locale if the locale didn't
     *                   change
     * @return true if the locale exists and was loaded successfully
     */
    public boolean setLocale(String localeName, boolean reload) {
        if (locale != null && locale.getName().equals(localeName)) {
            return !reload || locale.reloadMessages();
        } else {
            Locale l = Locale.loadLocale(this, localeName);
            if (l != null) {
                locale = l;
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Stop the plugin should an error occur
     */
    protected void emergencyStop() {
        emergencyStop = true;
        Bukkit.getPluginManager().disablePlugin(this);
    }

    /**
     * Get the plugin name
     */
    public abstract String getPluginName();

    /**
     * Get the plugin version
     */
    public abstract String getPluginVersion();

}
