package com.illuzionzstudios.command.type;

import com.illuzionzstudios.command.ReturnType;
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
 * Abstract implementation of a command
 */
public abstract class AbstractCommand extends Command {

    /**
     * The instance that executed this command
     */
    protected CommandSender commandSender;

    /**
     * This is only set if the {@link #commandSender} is a {@link Player}
     */
    protected Player player;

    /**
     * Required/Minimum permission to use command
     */
    protected IPermission requiredPermission;

    /**
     * Minimum arguments required to execute the command
     */
    protected int minArgs = 0;

    /**
     * String list of stored arguments given to the executor
     */
    protected List<String> args = new ArrayList<>();

    /**
     * Unique list of sub command executors
     * run when matches certain argument
     */
    private HashSet<AbstractCommand> subCommands = new HashSet<>();

    public AbstractCommand(String name, String... aliases) {
        super(name, "", "", Arrays.asList(aliases));
    }

    public AbstractCommand(String name) {
        super(name);
    }

    /**
     * Register a new sub command to the command
     *
     * @param command The sub command to add
     */
    public void addSubCommand(AbstractCommand command) {
        this.subCommands.add(command);
    }

    /**
     * Implemented to provide functionality to the command
     *
     * @param label Name of the command
     * @param args Parsed arguments
     * @return The return type for post processing
     */
    public abstract ReturnType onCommand(String label, String[] args);

    /**
     * @return Whether the command can be run by console
     */
    public abstract boolean isConsoleAllowed();

    /**
     * Ran when attempting to execute command
     */
    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        // Check if being executed by console
        if (!this.isConsoleAllowed() && commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage(ChatColor.DARK_RED + "The console cannot execute this command.");
            return true;
        }

        // Store parsed args
        this.args = Arrays.asList(args);

        // Provided too few arguments
        if (this.args.size() < minArgs) {
            new Message(getUsage()).sendMessage(commandSender);
            return true;
        }

        // Command instance executing
        // Could be a sub command which variables
        // will be dealt with
        AbstractCommand command = this;

        // Parse a sub command
        if (args.length >= 1) {
            command = findSubCommand(args[0]);

            if (command != null) {
                // Remove the first argument
                String[] newArgs = Arrays.copyOfRange(args, 1, args.length);

                // Set these new arguments to the parsed args
                command.args = Arrays.asList(newArgs);

                // Set instances executing command
                command.commandSender = commandSender;

                if (!(command.commandSender instanceof ConsoleCommandSender)) {
                    command.player = (Player) commandSender;
                }

                // Provided too few sub arguments
                if (newArgs.length < command.minArgs) {
                    new Message(getUsage()).sendMessage(commandSender);
                    return true;
                }
            }
        } else {
            // Only set local arguments if not a sub command
            this.commandSender = commandSender;

            if (!(commandSender instanceof ConsoleCommandSender)) {
                this.player = (Player) commandSender;
            }
        }

        // Check permissions
        if (command.requiredPermission != null) {
            if (!commandSender.hasPermission(command.requiredPermission.getPermissionNode()) && !commandSender.isOp()) {
                new Message("&cInsufficient permissions").sendPrefixedMessage(commandSender);
                return true;
            }
        }

        // Parse return types
        // Ignoring SUCCESS as we just let the command
        // run as normal
        switch (command.onCommand(label, args)) {
            case ERROR:
                new Message("&cA fatal error occurred executing this command").sendMessage(commandSender);
                break;
            case INVALID_ARGUMENTS:
                new Message(getUsage()).sendMessage(commandSender);
                break;
            case PLAYER_NOT_FOUND:
                new Message("&cThat player was not found").sendMessage(commandSender);
            case PLAYER_ONLY:
                new Message("&cOnly a player can execute a command like this").sendMessage(commandSender);
                break;
        }

        return true;
    }

    /**
     * Find sub command from string
     *
     * @param name The name or alias of command
     */
    public AbstractCommand findSubCommand(String name) {

        // Return null if there are no sub commands
        if (this.subCommands == null || this.subCommands.isEmpty()) return null;

        for (AbstractCommand cmd : this.subCommands) {
            // Check if it equals name or alias
            if (cmd.getName().equalsIgnoreCase(name) || cmd.getAliases().contains(name.toLowerCase())) return cmd;
        }

        return null;
    }

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
     * Lazy way if you don't want to register sub commands
     *
     * @param subName  Name of sub command
     * @param function Function as lambda to execute
     */
    @Deprecated
    public void sub(String subName, SubAction<Player> function) {
        if (sub(subName)) {
            function.execute(player);
        }
    }

    /**
     * Execute code if sub is called
     *
     * @param subName    Name of subcommand
     * @param permission Permission for the sub
     * @param function   Function as lambda to execute
     */
    @Deprecated
    public void sub(String subName, IPermission permission, SubAction<Player> function) {
        if (sub(subName)) {
            if (!commandSender.hasPermission(permission.getPermissionNode()) && !commandSender.isOp()) {
//                IlluzionzPlugin.getInstance().getLocale().getMessage("general.nopermission").sendPrefixedMessage(commandSender);
                new Message("&cInsufficient permissions").sendMessage(commandSender); // Until fix locale
                return;
            }
            function.execute(player);
        }
    }

    /**
     * Action for sub command
     */
    @Deprecated
    public interface SubAction<P extends Player> {
        void execute(P player);
    }

    /**
     * Argument readers
     */

    public boolean argIsSet(int idx) {
        return this.args.size() >= idx + 1;
    }

    public String argAsString(int idx, String def) {
        if (this.args.size() < idx + 1) {
            return def;
        }
        return this.args.get(idx);
    }

    public String argAsString(int idx) {
        return this.argAsString(idx, null);
    }

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

}
