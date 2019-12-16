package com.illuzionzstudios.core.bukkit.controller;

import com.illuzionzstudios.core.plugin.IlluzionzPlugin;

/**
 * Created by Illuzionz on 12 2019
 */
public interface BukkitController<P extends IlluzionzPlugin> {

    /**
     * Initialize stage
     */
    void initialize(P plugin);


    /**
     * Stop any invocation
     */
    void stop(P plugin);

}
