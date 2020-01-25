package com.illuzionzstudios.core.bukkit.command.type;

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
 * Command can be used by Console AND the player
 */
public abstract class GlobalCommand extends BaseCommand {

    public GlobalCommand(String name, String... aliases) {
        super(name, aliases);
    }

    public GlobalCommand(String name) {
        super(name);
    }

    /**
     * Called when command is executed
     *
     * @param label The command name
     * @param args  Arguments passed
     */
    public abstract void onCommand(String label, String[] args);

    @Override
    public boolean isConsoleAllowed() {
        return true;
    }
}
