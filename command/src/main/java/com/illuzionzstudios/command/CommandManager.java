package com.illuzionzstudios.command;

import com.illuzionzstudios.command.type.AbstractCommand;
import com.illuzionzstudios.core.bukkit.controller.BukkitController;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.HashMap;

public class CommandManager implements BukkitController<Plugin>, Listener {

    /**
     * Instance of command manager for registering commands
     */
    private static CommandManager INSTANCE;

    /**
     * @return Singleton instance of command manager
     */
    public static CommandManager get() {
        if (INSTANCE == null) {
            INSTANCE = new CommandManager();
        }

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
    public void register(AbstractCommand command) {
        try {
            // Prepare command map
            Field cMap = SimplePluginManager.class.getDeclaredField("commandMap");
            cMap.setAccessible(true);
            CommandMap map = (CommandMap) cMap.get(Bukkit.getPluginManager());


            // Remove existing commands
            Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");

            knownCommandsField.setAccessible(true);
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) knownCommandsField.get(map);
            knownCommandsField.setAccessible(false);

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
    }

}
