package com.illuzionzstudios.data.controller;

/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */

import com.illuzionzstudios.core.bukkit.controller.BukkitController;
import com.illuzionzstudios.core.util.Logger;
import com.illuzionzstudios.data.player.BukkitPlayer;
import com.illuzionzstudios.scheduler.MinecraftScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Controls all Bukkit players
 */
public abstract class BukkitPlayerController<P extends Plugin, BP extends BukkitPlayer> extends AbstractPlayerController<BP> implements Listener, BukkitController<P> {

    /**
     * Instances
     */
    public static BukkitPlayerController<?, ?> INSTANCE;
    public static Plugin PLUGIN;

    @Override
    public void initialize(P plugin) {
        INSTANCE = this;
        PLUGIN = plugin;

        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

        MinecraftScheduler.get().registerSynchronizationService(this);
    }

    @Override
    public void stop(P plugin) {
        // Save everyone
        for (BP player : new ArrayList<>(players)) {
            try {
                this.unregister(player);
            } finally {
                try {
                    player.unsafeSave();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                players.remove(player);
            }
        }

        // Save offline players
        getOfflineCache().forEach((uuid, player) -> {
            player.unsafeSave();
        });

        // Now disconnect database
        PlayerDataController.get().getDatabase().disconnect();
    }

    public BP getPlayer(String name) {
        return getPlayer(wp -> {
            Player player = wp.getBukkitPlayer();
            return player != null && wp.getName().equalsIgnoreCase(name);
        }).orElse(null);
    }

    public BP getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public BP getPlayer(CommandSender player) {
        return getPlayer(player.getName());
    }

    public BP getPlayer(LivingEntity entity) {
        return getPlayer(bp -> {
            Player player = bp.getBukkitPlayer();
            return player != null && player.getEntityId() == entity.getEntityId();
        }).orElse(null);
    }

    /**
     * Get all nearby players in radius
     *
     * @param location      From location
     * @param squaredRadius Radius
     */
    public List<? extends BP> getNearbyPlayers(Location location, int squaredRadius) {
        List<BP> playersNearby = new ArrayList<>();

        players.forEach(bp -> {
            if (bp.getBukkitPlayer() != null && bp.getLocation().getWorld().equals(location.getWorld()) && location.distanceSquared(bp.getLocation()) <= squaredRadius * squaredRadius) {
                if (!playersNearby.contains(bp)) {
                    playersNearby.add(bp);
                }
            }
        });

        return playersNearby;
    }

    /**
     * Pre process logging in
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        if (!PlayerDataController.get().getDatabase().isAlive()) {
            // Database down, disable plugin
            Logger.severe("Plugin disabling as could not establish connection to database");
            Bukkit.getPluginManager().disablePlugin(PLUGIN);
            return;
        }

//        Logger.info("%s is logging into the server", event.getName());

        BP player = this.handleLogin(event.getUniqueId(), event.getName());
    }

    /**
     * As a player logs in
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public final void onPlayerLogin(org.bukkit.event.player.PlayerLoginEvent event) {
        BP player = getPlayer(event.getPlayer());

        if (org.bukkit.event.player.PlayerLoginEvent.Result.KICK_FULL.equals(event.getResult())) {
            event.allow();
        }

        if (event.getResult() != org.bukkit.event.player.PlayerLoginEvent.Result.ALLOWED) {
            if (player != null) {
                handleLogout(player);
            }
        }
    }

    /**
     * When a player logs off
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLeave(PlayerQuitEvent event) {
        BP player = getPlayer(event.getPlayer());

        if (player == null) {
            return;
        }

        this.handleLogout(player);
    }
}
