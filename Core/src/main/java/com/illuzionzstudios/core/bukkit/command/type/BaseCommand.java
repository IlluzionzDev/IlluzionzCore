package com.illuzionzstudios.core.bukkit.command.type;

import com.illuzionzstudios.core.bukkit.permission.IPermission;
import com.illuzionzstudios.core.locale.Locale;
import com.illuzionzstudios.core.locale.player.Message;
import com.illuzionzstudios.core.plugin.IlluzionzPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */

public abstract class BaseCommand extends Command {

    /**
     * Only set for player command
     */
    protected Player player;

    /**
     * Required/Minimum permission to use command
     */
    protected IPermission requiredPermission;

    /**
     * Only set for console command
     */
    protected CommandSender commandSender;

    /**
     * Minimum required arguments
     */
    protected int minArgs;

    /**
     * Arguments for the command
     */
    private List<String> args = new ArrayList<>();

    /**
     * List of subcommands for this command
     * Blank if there are none
     */
    private HashSet<SubCommand> subCommands = new HashSet<>();

    public BaseCommand(String name, String... aliases) {
        super(name, "", "", Arrays.asList(aliases));
    }

    public BaseCommand(String name) {
        super(name);
    }

    /**
     * Can it be used by console
     *
     * @return Whether the command can be run by console
     */
    public abstract boolean isConsoleAllowed();

    public abstract boolean isPublic();

    /**
     * Register a new sub command to the command
     *
     * @param command The sub command to add
     */
    public void addSubCommand(SubCommand command) {
        this.subCommands.add(command);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (!this.isConsoleAllowed() && commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage(ChatColor.DARK_RED + "The console cannot execute this command.");
            return true;
        }

        this.args = Arrays.asList(args);

        if (this.args.size() < minArgs) {
            new Message(getUsage()).sendMessage(commandSender);
            return true;
        }

        if (this instanceof PlayerCommand) {
            this.player = (Player) commandSender;

            if (requiredPermission != null) {
                if (!player.hasPermission(requiredPermission.getPermissionNode()) && !commandSender.isOp()) {
                    IlluzionzPlugin.getInstance().getLocale().getMessage("general.nopermission").sendPrefixedMessage(commandSender);
                    return true;
                }
            }

            // Process sub commands
            SubCommand subCommand = findSubCommand(args[0]);

            if (subCommand != null) {
                String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
                subCommand.player = (Player) commandSender;
                this.commandSender = commandSender;

                if (subCommand.requiredPermission != null) {
                    if (!commandSender.hasPermission(subCommand.requiredPermission.getPermissionNode()) && !commandSender.isOp()) {
                        IlluzionzPlugin.getInstance().getLocale().getMessage("general.nopermission").sendPrefixedMessage(commandSender);
                        return true;
                    }
                }

                subCommand.onCommand(args[0], newArgs);
            } else {
                ((PlayerCommand) this).onCommand(s, args);
            }
        } else if (this instanceof GlobalCommand) {
            this.commandSender = commandSender;

            if (commandSender instanceof Player && !commandSender.isOp()) {
                if (requiredPermission != null) {
                    if (!commandSender.hasPermission(requiredPermission.getPermissionNode()) && !commandSender.isOp()) {
                        IlluzionzPlugin.getInstance().getLocale().getMessage("general.nopermission").sendPrefixedMessage(commandSender);
                        return true;
                    }
                }
            }

            // Process sub commands
            SubCommand subCommand = findSubCommand(args[0]);

            if (subCommand != null) {
                String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
                subCommand.commandSender = commandSender;

                if (subCommand.requiredPermission != null) {
                    if (!commandSender.hasPermission(subCommand.requiredPermission.getPermissionNode()) && !commandSender.isOp()) {
                        IlluzionzPlugin.getInstance().getLocale().getMessage("general.nopermission").sendPrefixedMessage(commandSender);
                        return true;
                    }
                }

                subCommand.onCommand(args[0], newArgs);
            } else {
                ((GlobalCommand) this).onCommand(s, args);
            }
        }

        return true;
    }

    // -------------------------------------------- //
    // Argument Readers
    // -------------------------------------------- //

    /**
     * Test if trying certain subcommand
     *
     * @param subName Name for subcommand
     * @return If first arg matches name
     */
    public boolean sub(String subName) {
        return argAsString(0).equalsIgnoreCase(subName);
    }

    /**
     * Execute code if sub is called
     *
     * @param subName    Name of subcommand
     * @param permission Permission for the sub
     * @param function   Function as lambda to execute
     */
    @Deprecated
    public void sub(String subName, IPermission permission, SubAction function) {
        if (sub(subName)) {
            if (!commandSender.hasPermission(permission.getPermissionNode()) && !commandSender.isOp()) {
                IlluzionzPlugin.getInstance().getLocale().getMessage("general.nopermission").sendPrefixedMessage(commandSender);
                return;
            }
            function.execute(player);
        }
    }

    /**
     * Execute code if sub is called
     *
     * @param subName  Name of subcommand
     * @param function Function as lambda to execute
     */
    @Deprecated
    public void sub(String subName, SubAction function) {
        if (sub(subName)) {
            function.execute(player);
        }
    }

    // Is set? ======================
    public boolean argIsSet(int idx) {
        return this.args.size() >= idx + 1;
    }

    // STRING ======================
    public String argAsString(int idx, String def) {
        if (this.args.size() < idx + 1) {
            return def;
        }
        return this.args.get(idx);
    }

    public String argAsString(int idx) {
        return this.argAsString(idx, null);
    }

    // INT ======================
    public Integer strAsInt(String str, Integer def) {
        if (str == null) {
            return def;
        }
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return def;
        }
    }

    public Integer argAsInt(int idx, Integer def) {
        return strAsInt(this.argAsString(idx), def);
    }

    public Integer argAsInt(int idx) {
        return this.argAsInt(idx, null);
    }

    // Double ======================
    public Double strAsDouble(String str, Double def) {
        if (str == null) {
            return def;
        }
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return def;
        }
    }

    public Double argAsDouble(int idx, Double def) {
        return strAsDouble(this.argAsString(idx), def);
    }

    public Double argAsDouble(int idx) {
        return this.argAsDouble(idx, null);
    }

    // TODO: Go through the str conversion for the other arg-readers as well.
    // Boolean ======================
    public Boolean strAsBool(String str) {
        str = str.toLowerCase();
        return str.startsWith("y") || str.startsWith("t") || str.startsWith("on") || str.startsWith("+") || str.startsWith("1") || str.startsWith("add");
    }

    public Boolean argAsBool(int idx, boolean def) {
        String str = this.argAsString(idx);
        if (str == null) {
            return def;
        }

        return strAsBool(str);
    }

    public Boolean argAsBool(int idx) {
        return this.argAsBool(idx, false);
    }

    // PLAYER ======================
    public Player strAsPlayer(String name, Player def, boolean msg) {
        Player ret = def;

        if (name != null) {
            Player player = Bukkit.getPlayer(name);
            if (player != null) {
                ret = player;
            }
        }

        if (msg && ret == null) {
            player.sendMessage(String.format(Locale.color("&cCouldn't find player %s"), name));
        }

        return ret;
    }

    public Player argAsPlayer(int idx, Player def, boolean msg) {
        return this.strAsPlayer(this.argAsString(idx), def, msg);
    }

    public Player argAsPlayer(int idx, Player def) {
        return this.argAsPlayer(idx, def, true);
    }

    public Player argAsPlayer(int idx) {
        return this.argAsPlayer(idx, null);
    }

    /**
     * Find sub command from string
     *
     * @param name The name or alias of command
     */
    public SubCommand findSubCommand(String name) {

        // Return null if there are no sub commands
        if (this.subCommands == null || this.subCommands.isEmpty()) return null;

        for (SubCommand cmd : this.subCommands) {
            // Check if it equals name or alias
            if (cmd.getName().equalsIgnoreCase(name) || cmd.getAliases().contains(name.toLowerCase())) return cmd;
        }

        return null;
    }

    /**
     * Action for subcommand
     */
    @Deprecated
    public interface SubAction<P extends Player> {
        void execute(P player);
    }
}
