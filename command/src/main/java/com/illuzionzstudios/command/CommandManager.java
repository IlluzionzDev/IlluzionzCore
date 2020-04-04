package com.illuzionzstudios.command;

import com.illuzionzstudios.command.type.BaseCommand;
import com.illuzionzstudios.command.type.GlobalCommand;
import com.illuzionzstudios.command.type.PlayerCommand;
import com.illuzionzstudios.core.bukkit.controller.BukkitController;
import com.illuzionzstudios.core.plugin.IlluzionzPlugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

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
 * Manage all commands
 */
public class CommandManager implements BukkitController<Plugin>, Listener {

    /**
     * Instance of command manager for registering commands
     */
    private static CommandManager INSTANCE = null;

    /**
     * Registered player commands
     */
    protected ArrayList<PlayerCommand> pCommands = new ArrayList<>();

    /**
     * Registered global commands
     */
    protected ArrayList<GlobalCommand> gCommands = new ArrayList<>();

    public CommandManager(Plugin plugin) {
        INSTANCE = this;
    }

    public static CommandManager get() {
        return INSTANCE;
    }

    @Override
    public void initialize(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void stop(Plugin plugin) {

    }

    /**
     * Registers a command
     *
     * @param command Base command
     */
    public void register(BaseCommand command) {
        try {
            //Prepare command map
            Field cMap = SimplePluginManager.class.getDeclaredField("commandMap");
            cMap.setAccessible(true);
            CommandMap map = (CommandMap) cMap.get(Bukkit.getPluginManager());


            //Remove existing commands
            Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) knownCommandsField.get(map);
            if (map.getCommand(command.getLabel()) != null) {
                knownCommands.remove(command.getLabel());
            }
            for (String alias : command.getAliases()) {
                if (map.getCommand(alias) != null) {
                    knownCommands.remove(alias);
                }
            }

            // Register the new command & aliases
            map.register(command.getLabel(), command);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // Register our player and global commands
        if (command instanceof PlayerCommand) {
            pCommands.add((PlayerCommand) command);
        } else if (command instanceof GlobalCommand) {
            gCommands.add((GlobalCommand) command);
        }
    }
}
