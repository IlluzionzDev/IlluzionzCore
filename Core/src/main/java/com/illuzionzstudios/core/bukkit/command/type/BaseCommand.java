package com.illuzionzstudios.core.bukkit.command.type;

import com.illuzionzstudios.core.locale.Locale;
import com.illuzionzstudios.core.locale.player.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseCommand extends Command {

    /**
     * Only set for player command
     */
    protected Player player;

    /**
     * Required/Minimum permission to use command
     */
    protected String requiredPermission;

    /**
     * Only set for console command
     */
    protected CommandSender commandSender;
    /**
     * Minimum required arguments
     */
    protected int minArgs;
    protected String notEnoughArgsMsg = "&cNot enough arguments";
    /**
     * Arguments for the command
     */
    private List<String> args = new ArrayList<>();

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

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (!this.isConsoleAllowed() && commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage(ChatColor.DARK_RED + "The console cannot execute this command.");
            return true;
        }

        this.args = Arrays.asList(args);

        if (this.args.size() < minArgs) {
            new Message(notEnoughArgsMsg).sendMessage(commandSender);
            return true;
        }

        if (this instanceof PlayerCommand) {
            this.player = (Player) commandSender;

            if (!player.hasPermission(requiredPermission) && !commandSender.isOp()) {
                new Message("&cNot enough permissions").sendMessage(commandSender);
                return true;
            }

            ((PlayerCommand) this).onCommand(s, args);
        } else if (this instanceof GlobalCommand) {
            this.commandSender = commandSender;

            if (commandSender instanceof Player && !commandSender.isOp()) {
                if (!player.hasPermission(requiredPermission) && !commandSender.isOp()) {
                    new Message("&cNot enough permissions").sendMessage(commandSender);
                    return true;
                }
            }


            ((GlobalCommand) this).onCommand(s, args);
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
     * @param subName  Name of subcommand
     * @param function Function as lambda to execute
     */
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
     * Action for subcommand
     */
    public interface SubAction<P extends Player> {
        void execute(P player);
    }
}
