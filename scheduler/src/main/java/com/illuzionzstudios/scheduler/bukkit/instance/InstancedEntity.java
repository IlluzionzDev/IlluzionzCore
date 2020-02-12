package com.illuzionzstudios.scheduler.bukkit.instance;

import com.illuzionzstudios.scheduler.object.Destructible;
import com.illuzionzstudios.scheduler.Tickable;
import org.bukkit.entity.Player;

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
 * An object that is instanced
 */
public interface InstancedEntity extends Tickable, Destructible {
    // Represents instanced entity //

    Player getPlayer();

    /**
     * Informs user about an error that occurred
     *
     * @param message The error message
     */
    default void informError(String message) {

        if (this.getPlayer() != null) {
            this.getPlayer().sendMessage(message);
        }
    }

    /**
     * Kick player if instance couldn't be destroyed
     *
     * @param kickMessage The error message
     */
    default void kickPlayer(String kickMessage) {
        if (this.getPlayer() != null) {
            this.getPlayer().kickPlayer(kickMessage);
        }
    }

}
