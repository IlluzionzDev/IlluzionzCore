package com.illuzionzstudios.ui.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.illuzionzstudios.core.bukkit.controller.BukkitController;
import com.illuzionzstudios.core.plugin.IlluzionzPlugin;
import com.illuzionzstudios.scheduler.MinecraftScheduler;
import com.illuzionzstudios.scheduler.bukkit.instance.InstancedEntity;
import com.illuzionzstudios.scheduler.sync.Sync;
import com.illuzionzstudios.ui.button.InterfaceButton;
import com.illuzionzstudios.ui.button.InterfaceClickListener;
import com.illuzionzstudios.ui.types.IUserInterface;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
 * Controls all interactive actions by com.illuzionzstudios.data.player in interfaces
 */
public class InterfaceController<P extends IlluzionzPlugin> implements Listener, BukkitController {


    public static InterfaceController INSTANCE;
    // An active interface is constituted as any interface that needs to be listening for clicks //
    @Sync
    @Getter
    private Set<IUserInterface> activeInterfaces = Sets.newConcurrentHashSet();

    public InterfaceController(P plugin) {
        INSTANCE = this;
        initialize(plugin);
    }

    @Override
    public void initialize(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        MinecraftScheduler.get().registerSynchronizationService(this);
    }

    @Override
    public void stop(Plugin plugin) {

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        for (IUserInterface ui : activeInterfaces) {
            for (HumanEntity entity : ui.getInventory().getViewers()) {
                if (entity.getUniqueId() == event.getWhoClicked().getUniqueId()) {
                    //We found the interface, now cancel the event and find the button they activated.
                    event.setCancelled(true);
                    Player player = (Player) event.getWhoClicked();

                    if (!ui.isClickable()) {
                        continue;
                    }
                    //They clicked on a button in the inventory
                    for (InterfaceButton button : ui.getButtons()) {
                        if (button.getSlot() == event.getRawSlot()) {
                            if (button.getListener() != null) {
                                safelyClick(button.getListener(), player, event);
                            }
                        }
                    }

                    //They clicked in the com.illuzionzstudios.data.player inventory
                    if (event.getRawSlot() >= ui.getInventory().getSize()) {
                        if (ui.getPlayerInventoryListener() != null) {
                            safelyClick(ui.getPlayerInventoryListener(), player, event);
                        }
                    }

                    //They clicked on one of the specific slot listeners
                    for (Map.Entry<Integer, InterfaceClickListener> current : ui.getSlotListeners().entrySet()) {
                        if (current.getKey() == event.getRawSlot()) {
                            safelyClick(current.getValue(), player, event);
                        }
                    }

                    return;
                }
            }
        }
    }

    private void safelyClick(InterfaceClickListener listener, Player player, InventoryClickEvent event) {
        MinecraftScheduler.get().safelyTick(new InstancedEntity() {
            @Override
            public void tick() {
                listener.onClick(player, event);
            }

            @Override
            public boolean isValid() {
                return true;
            }

            @Override
            public void destroy() {
                // Nothing
            }

            @Override
            public Player getPlayer() {
                return player;
            }
        });
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Iterator<IUserInterface> itr = activeInterfaces.iterator();
        while (itr.hasNext()) {
            IUserInterface ui = itr.next();
            Lists.newArrayList(ui.getInventory().getViewers()).forEach(humanEntity -> {
                if (humanEntity.getUniqueId() == event.getPlayer().getUniqueId()) {
                    if (ui.onClose()) {
                        itr.remove();
                    }
                }
            });
        }
    }

}
