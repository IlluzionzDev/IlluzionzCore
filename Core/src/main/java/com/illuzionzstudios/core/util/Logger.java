package com.illuzionzstudios.core.util;

import com.illuzionzstudios.core.IlluzionzCore;

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
        IlluzionzCore.LOGGER.info(String.format(message.toString(), parameters));
    }

}
