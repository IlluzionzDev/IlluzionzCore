package com.illuzionzstudios.core.util;

import com.illuzionzstudios.core.IlluzionzCore;
import com.illuzionzstudios.core.plugin.IlluzionzPlugin;
import io.netty.handler.logging.LogLevel;

import java.util.logging.Level;

/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */

public class Logger {

    public static void debug(Object message) {
        log(Level.WARNING, "[DEBUG] " + message);
    }

    public static void debug(String message, Object... parameters) {
        log(Level.WARNING, "[DEBUG] " + message, parameters);
    }

    public static void warn(String message, Object... parameters) {
        log(Level.WARNING, "[WARN] " + message, parameters);
    }

    public static void severe(String message, Object... parameters) {
        log(Level.SEVERE, "[SEVERE] " + message, parameters);
    }

    public static void info(String message, Object... parameters) {
        log(Level.INFO, message, parameters);
    }

    private static void log(Level level, Object message, Object... parameters) {
        IlluzionzPlugin.getInstance().getLogger().log(level, String.format(message.toString(), parameters));
    }

}
