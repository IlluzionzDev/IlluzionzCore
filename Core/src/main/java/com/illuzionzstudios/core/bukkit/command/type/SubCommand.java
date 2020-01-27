package com.illuzionzstudios.core.bukkit.command.type;

import com.illuzionzstudios.core.bukkit.permission.IPermission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
 * Sub command to be used
 */
public abstract class SubCommand {

    /**
     * Name of the sub command
     */
    public String name;

    /**
     * Alternatives to use this sub command
     */
    public List<String> aliases = new ArrayList<>();

    /**
     * Required/Minimum permission to use sub command
     */
    protected IPermission requiredPermission;

    /**
     * Only set for console command
     */
    protected CommandSender commandSender;

    /**
     * Only set for player sender
     */
    protected Player player;

    public SubCommand(String name, String... aliases) {
        this.name = name;
        this.aliases = Arrays.asList(aliases);
    }

    /**
     * Called when sub command is executed
     *
     * @param label The command name
     * @param args  Arguments passed
     */
    public abstract void onCommand(String label, String[] args);

}
