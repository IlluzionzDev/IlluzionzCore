package com.illuzionzstudios.ui.types;

import com.illuzionzstudios.core.locale.player.Message;
import com.illuzionzstudios.scheduler.MinecraftScheduler;
import com.illuzionzstudios.ui.button.InterfaceButton;
import com.illuzionzstudios.ui.button.InterfaceClickListener;
import com.illuzionzstudios.ui.controller.InterfaceController;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
 * A custom inventory which a com.illuzionzstudios.data.player can interact with
 */
@Getter
@NoArgsConstructor
public abstract class UserInterface implements IUserInterface {

    /**
     * The bukkit inventory reference
     */
    protected Inventory inventory;

    /**
     * The bukkit player
     */
    protected Player player;

    /**
     * Rendered buttons
     */
    protected List<InterfaceButton> buttons = new CopyOnWriteArrayList<>();

    /**
     * Custom slot listeners
     */
    HashMap<Integer, InterfaceClickListener> slotListeners = new HashMap<>();

    /**
     * If false, no listeners will be called
     */
    @Setter
    private boolean clickable = true;

    /**
     * If the interface was forcibly closed by us
     */
    @Getter
    private boolean forciblyClosed = false;

    /**
     * Listener for when com.illuzionzstudios.data.player clicks there own inventory
     * if null no listeners will be called
     */
    @Setter
    private InterfaceClickListener playerInventoryListener;

    public UserInterface(Player player) {
        open(player);
    }

    public void build() {
        generateInventory();
        render();
    }

    public void open(Player player) {
        open(player, true);
    }

    public void openInventory(Player player) {
        InterfaceController.INSTANCE.getActiveInterfaces().add(this);
        player.openInventory(inventory);
    }

    public void open(boolean build, Player... players) {
        try {
            MinecraftScheduler.get().validateMainThread();
            this.player = player;

            if (build) {
                build();
            }
            for (Player player : players) {
                openInventory(player);
            }

        } catch (Exception e) {
            e.printStackTrace();
            for (Player player : players) {
                new Message("&cCould not open interace").sendMessage(player);
                player.closeInventory();
            }
            InterfaceController.INSTANCE.getActiveInterfaces().remove(this);
            onClose();
        }
    }

    public void open(Player player, boolean build) {
        try {
            MinecraftScheduler.get().validateMainThread();
            this.player = player;

            if (build) {
                build();
            }

            openInventory(player);
        } catch (Exception e) {
            e.printStackTrace();
            new Message("&cCould not open interface").sendMessage(player);
            InterfaceController.INSTANCE.getActiveInterfaces().remove(this);
            onClose();
            player.closeInventory();
        }
    }

    /**
     * Forcefully close the interface
     */
    public void close() {
        InterfaceController.INSTANCE.getActiveInterfaces().remove(this);
        this.forciblyClosed = true;
        onClose();
        player.closeInventory();
    }

    public void render() {
        inventory.clear();

        // Renders buttons //
        for (InterfaceButton button : buttons) {
            if (button.getSlot() < inventory.getSize()) {
                // Render translate in menu's
                inventory.setItem(button.getSlot(), button.getIcon());
            }
        }
    }

    // Excludes null items //
    public int size() {
        int i = 0;

        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                i++;
            }
        }

        return i;
    }

    protected int round(int num) {
        return Math.max(9, num > 54 ? 54 : (num % 9 == 0) ? num : ((num / 9) + 1) * 9);
    }

    protected int round(int num, int size) {
        return Math.max(9, num > size ? size : (num % 9 == 0) ? num : ((num / 9) + 1) * 9);
    }

    public abstract void generateInventory();

    //will be called upon inventory close event
    public boolean onClose() {
        return true;
    }

    public void tick() {
        // Optional //
    }

    public void rename(String newName) {
        inventory = Bukkit.createInventory(inventory.getHolder(), inventory.getSize(), newName);
        render();
        player.closeInventory();
        player.openInventory(inventory);
    }


    public void fillEmptySpaces(InterfaceButton button) {
        for (int i = 0; i < inventory.getSize(); i++) {
            int finalI = i;
            if (this.getButtons().stream().noneMatch(b -> b.getSlot() == finalI)) {
                button = button.clone();
                button.setSlot(finalI);
                this.addButton(button);
            }
        }
    }

    //Button helpers
    public InterfaceButton getButton(String id) {
        for (InterfaceButton button : buttons) {
            if (id.equalsIgnoreCase(button.getId())) {
                return button;
            }
        }
        return null;
    }

    public InterfaceButton getButton(int slot) {
        return buttons.stream().filter(button -> button.getSlot() == slot).findAny().orElse(null);
    }


    public void addButton(InterfaceButton button) {
        //Remove any button already in that slot...
        buttons.removeIf(b -> b.getSlot() == button.getSlot());
        //Add the new button
        buttons.add(button);
    }

    public void addSlotListener(int slot, InterfaceClickListener listener) {
        slotListeners.put(slot, listener);
    }
}
