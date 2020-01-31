package com.illuzionzstudios.core.util;

import com.illuzionzstudios.core.IlluzionzCore;
import com.illuzionzstudios.core.scheduler.MinecraftScheduler;

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
        log("[DEBUG] " + message);
    }

    public static void debug(String message, Object... parameters) {
        log("[DEBUG] " + message, parameters);
    }

    public static void severe(String message, Object... parameters) {
        log("[SEVERE] " + message, parameters);
    }

    public static void info(String message, Object... parameters) {
        log(message, parameters);
    }

    public static void logStackTrace() {
        Logger.severe("Stack trace requested....");
        for (StackTraceElement ste : new Throwable().getStackTrace()) {
            Logger.severe(ste.toString());
        }
    }

    private static void log(Object message, Object... parameters) {
        // This will indicate this is not being ran on main thread //
        boolean asyncTag = false;

        MinecraftScheduler scheduler = MinecraftScheduler.get();

        if (scheduler != null) {
            try {
                scheduler.validateMainThread();
            } catch (Exception expection) {
                asyncTag = true;
            }
        }

        IlluzionzCore.LOGGER.info((asyncTag ? "[ASYNC] " : "") + String.format(message.toString(), parameters));
    }

}
