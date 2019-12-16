package com.illuzionzstudios.core.bukkit.command.type;

/**
 * Command used only by the player
 */
public abstract class PlayerCommand extends BaseCommand {

    public PlayerCommand(String name, String... aliases) {
        super(name, aliases);
    }

    public PlayerCommand(String name) {
        super(name);
    }

    /**
     * Called when command is executed
     *
     * @param label The command name
     * @param args Arguments passed
     */
    public abstract void onCommand(String label, String[] args);

    @Override
    public boolean isConsoleAllowed() {
        return false;
    }
}
