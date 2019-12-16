package com.illuzionzstudios.core.bukkit.command.type;

/**
 * Command can be used by Console AND the player
 */
public abstract class GlobalCommand extends BaseCommand {

    public GlobalCommand(String name, String... aliases) {
        super(name, aliases);
    }

    public GlobalCommand(String name) {
        super(name);
    }

    /**
     * Called when command is executed
     *
     * @param label The command name
     * @param args Arguments passed
     */
    public abstract void onCommand(String label,  String[] args);

    @Override
    public boolean isConsoleAllowed() {
        return true;
    }
}
