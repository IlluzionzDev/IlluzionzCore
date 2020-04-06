package com.illuzionzstudios.data.player;

/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */

import com.illuzionzstudios.core.locale.player.Message;
import com.illuzionzstudios.core.plugin.IlluzionzPlugin;
import com.illuzionzstudios.core.util.Logger;
import com.illuzionzstudios.data.PlayerData;
import com.illuzionzstudios.data.controller.PlayerDataController;
import com.illuzionzstudios.scheduler.MinecraftScheduler;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Player abstraction for data loading/saving
 */
@Getter
public abstract class AbstractPlayer {

    /**
     * Cached name of the player
     */
    private String name;

    /**
     * Identifier of the player
     */
    private UUID uuid;

    /**
     * Keys in the data to always be replaced
     */
    private HashMap<String, String> keyMetadata = new HashMap<>();

    /**
     * Keys in the data that have been modified
     * Used for tracking whether to bother setting data
     */
    private CopyOnWriteArrayList<String> modifiedKeys = new CopyOnWriteArrayList<>();

    /**
     * Local data stored before being saved
     */
    private HashMap<String, Object> cachedData = new HashMap<>();

    /**
     * Player data associated with this player
     */
    private ArrayList<AbstractPlayerData<?>> data = new ArrayList<>();

    /**
     * If the player data has been loading into the cache
     */
    private AtomicBoolean loaded = new AtomicBoolean(false);

    public AbstractPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUUID() {
        return uuid;
    }

    /**
     * Get player data from a class
     *
     * @param type The class type
     * @param <T> Player data type to return
     */
    public <T extends PlayerData<?>> T get(Class<T> type) {
        for (AbstractPlayerData<?> info : data) {
            if (info.getClass() == type || type.isAssignableFrom(info.getClass())) {
                return (T) info;
            }
        }

        return PlayerDataController.INSTANCE.getDefaultData(type);
    }

    /**
     * Set that a key has been modified in data
     *
     * @param key Key to set modified
     */
    public void modifyKey(String key) {
        if (!modifiedKeys.contains(key)) {
            modifiedKeys.add(key);
        }
    }

    /**
     * Get a message straight from locale
     *
     * @param key Key to fetch from locale
     */
    public Message getMessage(String key) {
        return IlluzionzPlugin.getInstance().getLocale().getMessageOrDefault(key, key);
    }

    /**
     * Reset any keys we modified
     */
    public void resetModifiedKeys() {
        modifiedKeys.clear();
    }

    /**
     * Called when loading into server
     */
    public void load() {
        // Shouldn't try to load twice
        if (loaded.get()) return;

        // If stored data is empty, try upload cached data first
        if (PlayerDataController.get().getDatabase().getFields(this).isEmpty()) {
            upload();
        }

        // Loading stored data into cache
        PlayerDataController.get().getDatabase().getFields(this).forEach((field, value) -> {
            // Simply insert into cached data
            this.cachedData.put(field, value);
        });

//        Logger.info("%s's player data loaded into server.", name);

        this.loaded.set(true);
        PlayerDataController.get().applyDefaultData(this);
    }

    /**
     * Shorthand for just saving
     */
    public void save() {
        this.save(null);
    }

    /**
     * Save cached data to the database
     *
     * CAN BE PERFORMED ON MAIN THREAD
     *
     * @param doAfter Perform an action afterwards
     */
    public void save(Consumer<Boolean> doAfter) {
        prepareSaveData();

        // Saving data async
        MinecraftScheduler.get().desynchronize(this::upload, consumer -> {
            try {
                boolean insert = consumer.get();
                if (doAfter != null) {
                    doAfter.accept(insert);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Quick save
     */
    public void unsafeSave() {
        prepareSaveData();
        upload();
    }

    /**
     * Upload cached data into database
     *
     * NEVER SERVER THREAD SAFE
     */
    public boolean upload() {
//        Logger.info("Saving %s's player data.", name);

        // Upload modified data
        this.modifiedKeys.forEach((field) -> {
            Object value = this.cachedData.getOrDefault(field, null);

            // Don't save if nothing to save
            if (value == null) return;

            // Set the field in the database
            PlayerDataController.get().getDatabase().setFieldValue(this, field, value);
        });

        resetModifiedKeys();

        return true;
    }

    /**
     * Get data ready to save
     */
    public void prepareSaveData() {
        data.forEach(data -> {
            try {
                data.onSave();
            } catch (Exception e) {
                Logger.severe("Error occurred while preparing save data");
                e.printStackTrace();
            }
        });
    }

}
