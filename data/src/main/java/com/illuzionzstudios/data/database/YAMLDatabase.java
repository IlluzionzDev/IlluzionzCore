package com.illuzionzstudios.data.database;

/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */

import com.illuzionzstudios.config.Config;
import com.illuzionzstudios.core.plugin.IlluzionzPlugin;
import com.illuzionzstudios.data.player.AbstractPlayer;

import java.util.HashMap;

/**
 * Database stored in flat files
 */
public class YAMLDatabase implements Database {

    @Override
    public HashMap<String, Object> getFields(AbstractPlayer player) {
        // Local data file
        Config dataConfig = new Config(IlluzionzPlugin.getInstance(), "data", player.getUUID() + ".yml");

        HashMap<String, Object> cache = new HashMap<>();

        // Get keys and load if found value
        if (dataConfig.getDefaultSection() == null) return cache;

        dataConfig.getDefaultSection().getKeys(true).forEach(path -> {
            // Check if not null value
            if (dataConfig.get(path) != null) {
                // Add to cache
                cache.put(path, dataConfig.get(path));
            }
        });

        return cache;
    }

    @Override
    public Object getFieldValue(AbstractPlayer player, String queryingField) {
        // Local data file
        Config dataConfig = new Config(IlluzionzPlugin.getInstance(), "data", player.getUUID() + ".yml");

        return dataConfig.get(queryingField);
    }

    @Override
    public void setFieldValue(AbstractPlayer player, String queryingField, Object value) {
        // Local data file
        Config dataConfig = new Config(IlluzionzPlugin.getInstance(), "data", player.getUUID() + ".yml");

        dataConfig.set(queryingField, value);
        dataConfig.saveChanges();
    }

    @Override
    public boolean connect() {
        return false;
    }

    @Override
    public boolean disconnect() {
        return false;
    }

    @Override
    public boolean isAlive() {
        return true;
    }
}
