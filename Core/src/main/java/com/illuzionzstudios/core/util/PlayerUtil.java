package com.illuzionzstudios.core.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */

public class PlayerUtil {

    /**
     * Get all online players as a {@link List}
     */
    public static List<Player> getPlayers() {
        Object o = Bukkit.getOnlinePlayers();
        List<Player> players = new ArrayList<>();
        Collection<?> c = (Collection<?>) o;

        for (Object a : c) {
            if (a instanceof Player)
                players.add((Player) a);
        }

        return players;
    }

}
