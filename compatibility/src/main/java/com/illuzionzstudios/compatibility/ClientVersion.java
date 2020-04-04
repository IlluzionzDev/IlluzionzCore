package com.illuzionzstudios.compatibility;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */

/**
 * Handy reference for checking a connected client's Minecraft version
 */
public class ClientVersion {

    static HashMap<UUID, ServerVersion> players = new HashMap<>();

    /**
     * Check to see what client version this player is connected to the server
     * with. Note that if a player is connecting with a newer client than the server,
     * this value will simply be the server version.
     *
     * @param player Player to check
     * @return ServerVersion that matches this player's Minecraft version
     */
    public static ServerVersion getClientVersion(Player player) {
        if (player == null || !players.containsKey(player.getUniqueId())) {
            return ServerVersion.getServerVersion();
        }
        return players.get(player.getUniqueId());
    }

    private static ServerVersion protocolToVersion(int version) {
        // https://github.com/ViaVersion/ViaVersion/blob/master/common/src/main/java/us/myles/ViaVersion/api/protocol/ProtocolVersion.java
        // https://github.com/ProtocolSupport/ProtocolSupport/blob/master/src/protocolsupport/api/ProtocolVersion.java
        switch (version) {
            case 4:
            case 5:
                return ServerVersion.V1_7;
            case 47:
                return ServerVersion.V1_8;
            case 107:
            case 108:
            case 109:
            case 110:
                return ServerVersion.V1_9;
            case 210:
                return ServerVersion.V1_10;
            case 315:
            case 316:
                return ServerVersion.V1_11;
            case 335:
            case 338:
            case 340:
                return ServerVersion.V1_12;
            case 393:
            case 401:
            case 404:
                return ServerVersion.V1_13;
            case 477:
            case 485:
            case 490:
            case 498:
                return ServerVersion.V1_14;
        }
        return version > 498 ? ServerVersion.getServerVersion() : ServerVersion.UNKNOWN;
    }
}
