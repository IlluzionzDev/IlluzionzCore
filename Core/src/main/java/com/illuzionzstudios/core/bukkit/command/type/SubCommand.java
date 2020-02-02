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
public abstract class SubCommand extends BaseCommand {

    public SubCommand(String name, String... aliases) {
        super(name, aliases);
    }

    /**
     * Called when sub command is executed
     *
     * @param label The command name
     * @param args  Arguments passed
     */
    public abstract void onCommand(String label, String[] args);

}
