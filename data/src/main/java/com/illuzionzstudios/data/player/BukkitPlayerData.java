package com.illuzionzstudios.data.player;

import net.minecraft.server.v1_15_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */

/*
 * Player data which can be a bukkit listener
 */
public abstract class BukkitPlayerData<BP extends BukkitPlayer> extends AbstractPlayerData<BP> implements Listener {

    private boolean eventsRegistered = false;

    public BukkitPlayerData(BP player) {
        super(player);
    }

    @Override
    public void unregister() {
        super.unregister();

        if (eventsRegistered) {
            HandlerList.unregisterAll(this);
        }
    }

    protected void registerEvents(Plugin plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        eventsRegistered = true;
    }


    public void onEntityTick(EntityPlayer player) {
    }
}
