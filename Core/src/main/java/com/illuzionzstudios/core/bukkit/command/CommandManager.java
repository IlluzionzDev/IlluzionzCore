package com.illuzionzstudios.core.bukkit.command;

/**
 * Created by Illuzionz on 12 2019
 */

import com.illuzionzstudios.core.bukkit.command.type.BaseCommand;
import com.illuzionzstudios.core.bukkit.command.type.GlobalCommand;
import com.illuzionzstudios.core.bukkit.command.type.PlayerCommand;
import com.illuzionzstudios.core.bukkit.controller.BukkitController;
import com.illuzionzstudios.core.plugin.IlluzionzPlugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manage all commands
 */
public class CommandManager implements BukkitController, Listener {

    private static CommandManager INSTANCE = null;

    @Getter
    protected ArrayList<String> allowed = new ArrayList<>();

    protected ArrayList<PlayerCommand> pCommands = new ArrayList<PlayerCommand>();
    protected ArrayList<GlobalCommand> gCommands = new ArrayList<GlobalCommand>();

    public CommandManager(IlluzionzPlugin plugin) {
        INSTANCE = this;
    }

    public static CommandManager get() {
        return INSTANCE;
    }

    private static int getSimilarityScore(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] similar = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    similar[j] = j;
                } else {
                    if (j > 0) {
                        int newValue = similar[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                            newValue = Math.min(Math.min(newValue, lastValue), similar[j]) + 1;
                        }
                        similar[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) {
                similar[s2.length()] = lastValue;
            }
        }
        return similar[s2.length()];
    }

    @Override
    public void initialize(IlluzionzPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void stop(IlluzionzPlugin plugin) {

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

            //Register the new command & aliases
            map.register(command.getLabel(), command);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        allowed.add(command.getName());
        allowed.addAll(command.getAliases());

        if (command instanceof PlayerCommand) {
            pCommands.add((PlayerCommand) command);
        } else if (command instanceof GlobalCommand) {
            gCommands.add((GlobalCommand) command);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPreCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (player == null) {
            event.getPlayer().sendMessage(ChatColor.DARK_RED + "You can't use commands right now!");
            event.setCancelled(true);
            return;
        }

        String attempted = event.getMessage().split(" ")[0];
        String soFar = null;
        if (!canUseCommand(attempted, player)) {
            event.setCancelled(true);

            if (attempted.length() <= 1) {
                player.sendMessage(ChatColor.DARK_RED + "Invalid command... Type /help for a list of commands");
                return;
            }

            for (String s : allowed) {
                int score = getSimilarityScore(attempted, s);
                if (!attempted.isEmpty() && score <= 3 && attempted.toCharArray()[1] == s.toCharArray()[0]) {
                    player.sendMessage(ChatColor.DARK_RED + "Did you mean " + ChatColor.RED + "/" + s + "? " + ChatColor.DARK_RED + "If not, type /help to see the commands!");
                    return;
                }
            }
            player.sendMessage(ChatColor.DARK_RED + "Invalid command... Type /help for a list of commands");
        }
    }

    private boolean canUseCommand(String msg, Player player) {
        if (player.isOp()) {
            return true;
        }

        for (String s : allowed) {
            if (msg.equalsIgnoreCase("/" + s)) {
                return true;
            }
        }

        return false;
    }
}
