/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */
package com.illuzionzstudios.data.controller;

import com.illuzionzstudios.core.plugin.IlluzionzPlugin;
import com.illuzionzstudios.data.player.GamePlayer;

import java.util.UUID;

/**
 * Default game player controllers
 */
public class GamePlayerController extends BukkitPlayerController<IlluzionzPlugin, GamePlayer> {

    public static GamePlayerController INSTANCE;

    @Override
    protected GamePlayer newInstance(UUID uuid, String s) {
        return new GamePlayer(uuid, s);
    }

    @Override
    public void initialize(IlluzionzPlugin plugin) {
        super.initialize(plugin);
        INSTANCE = this;
    }

}
