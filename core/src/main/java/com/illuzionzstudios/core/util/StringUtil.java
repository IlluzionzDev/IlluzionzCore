package com.illuzionzstudios.core.util;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */

public class StringUtil {
    private final static int CENTER_PX = 154;
    private static String[] suffix = new String[]{"", "k", "m", "b", "t"};
    private static int MAX_LENGTH = 4;

    public static int getColoredBars(int nominator, int denominator, int size) {
        return (nominator * size) / denominator;
    }

    public static String getBar(float coloredBars, int size) {
        return getBar(null, coloredBars, 0, size);
    }

    public static String getBar(ChatColor color, float coloredBars, float negatedBars, int size) {
        return getBar(color, "|", coloredBars, negatedBars, size);
    }

    public static String getBar(ChatColor color, String character, float coloredBars, int size) {
        return getBar(color, character, coloredBars, 0, size);
    }

    /**
     * @param maxLength Pixels to trim to
     */
    public static String getTrimmedText(int maxLength, String text) {
        int messagePxSize = 0;
        int subLength = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : text.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                if (c == 'l' || c == 'L') {
                    isBold = true;
                } else {
                    isBold = false;
                }
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;

                // Increment only if no bold as
                // if bold we don't want to substring that
                if (!isBold) subLength++;
            }

            if (messagePxSize > maxLength) {
                // Return sub
                return text.substring(0, subLength);
            }
        }

        return text;
    }

    public static String trimEnd(String value) {
        int len = value.length();
        int st = 0;
        while ((st < len) && value.charAt(len - 1) == ' ') {
            len--;
        }
        return value.substring(0, len);
    }

    public static String getBar(ChatColor color, String character, float coloredBars, float negatedBars, int size) {
        StringBuilder bar = new StringBuilder();

        float percent = (coloredBars * 100) / size;

        for (int i = 0; i < size; i++) {
            boolean colored = false;
            boolean negative = false;

            if (coloredBars > 0) {
                colored = true;
                coloredBars--;
            } else if (negatedBars > 0) {
                negative = true;
                colored = true;
                negatedBars--;
            }

            if (color == null) {
                if (percent > 50) {
                    color = ChatColor.GREEN;
                } else if (percent >= 20D && percent <= 50D) {
                    color = ChatColor.GOLD;
                } else {
                    color = ChatColor.DARK_RED;
                }
            }

            if (negative) {
                color = ChatColor.RED;
            }

            bar.append(colored ? color : ChatColor.GRAY).append(character);
        }
        return bar.toString();
    }

    public static String getCenteredMessage(String message) {
        return getCenteredMessage(CENTER_PX, message);
    }

    public static String getCenteredMessage(int px, String message) {
        if (message == null || message.equals("")) {
            return message;
        }

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
                continue;
            } else if (previousCode) {
                previousCode = false;
                if (c == 'l' || c == 'L') {
                    isBold = true;
                    continue;
                } else {
                    isBold = false;
                }
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = px - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();

        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb.toString() + message;
    }

    public static BaseComponent[] getCenteredMessage(BaseComponent[] baseComponents) {
        if (baseComponents == null) {
            return baseComponents;
        }

        StringBuilder totalText = new StringBuilder();
        for (BaseComponent component : baseComponents) {
            totalText.append(component.toLegacyText());
        }

        String spaces = getCenteredMessage(totalText.toString());
        spaces = spaces.replace(totalText.toString(), "");

        BaseComponent[] newComponent = new BaseComponent[baseComponents.length + 1];
        newComponent[0] = new TextComponent(spaces);
        for (int i = 1; i < baseComponents.length + 1; i++) {
            newComponent[i] = baseComponents[i - 1];
        }
        return newComponent;
    }

    public static String replaceLast(String string, String toReplace, String replacement) {
        int index = string.lastIndexOf(toReplace);
        if (index == -1) {
            return string;
        }
        return string.substring(0, index) + replacement + string.substring(index + toReplace.length());
    }

    public static String getFormattedTime(long millis, boolean verbose) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long daysLeft = TimeUnit.MILLISECONDS.toDays(millis);
        millis = millis - TimeUnit.DAYS.toMillis(daysLeft);
        long hoursLeft = TimeUnit.MILLISECONDS.toHours(millis);
        millis = millis - TimeUnit.HOURS.toMillis(hoursLeft);
        long minutesLeft = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis = millis - TimeUnit.MINUTES.toMillis(minutesLeft);
        long secondsLeft = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder message = new StringBuilder();
        if (daysLeft != 0) {
            message.append(daysLeft);
            message.append((verbose ? " days" : "d"));
        }

        if (hoursLeft != 0) {
            if (message.length() != 0) {
                message.append((verbose ? ", " : " "));
            }
            message.append(hoursLeft);
            message.append((verbose ? " hour" : "h"));
            if (verbose && hoursLeft > 1) {
                message.append("s");
            }
        }

        if (minutesLeft != 0) {
            if (message.length() != 0) {
                message.append((verbose ? ", " : " "));
            }
            message.append(minutesLeft);
            message.append((verbose ? " minute" : "m"));
            if (verbose && minutesLeft > 1) {
                message.append("s");
            }
        }

        //Only display seconds if waittime is <1 hr
        if (secondsLeft != 0 && hoursLeft == 0 && daysLeft == 0) {
            if (message.length() != 0) {
                message.append((verbose ? ", " : " "));
            }
            message.append(secondsLeft);
            message.append((verbose ? " second" : "s"));
            if (verbose && secondsLeft > 1) {
                message.append("s");
            }
        }

        //Only display seconds if waittime is <1 sec
        if (hoursLeft == 0 && minutesLeft == 0 && secondsLeft == 0 && millis > 0) {
            if (message.length() != 0) {
                message.append((verbose ? ", " : " "));
            }
            message.append(millis);
            message.append((verbose ? " millis" : "ms"));
        }
        String formatted = message.toString();
        if (verbose) {
            formatted = StringUtil.replaceLast(formatted, ", ", " and ");
        }
        return formatted;
    }

    public static boolean contains(String string, CharSequence contains) {
        if (string == null) {
            return false;
        }
        return string.indexOf(contains.toString()) > -1;
    }

    public static String shorten(double number) {
        return shorten(number, MAX_LENGTH);
    }

    public static String shorten(double number, int length) {
        String r = new DecimalFormat("##0E0").format(number);
        r = r.replaceAll("E[0-9]", suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
        while (r.length() > length || r.matches("[0-9]+\\.[a-z]")) {
            r = r.substring(0, r.length() - 2) + r.substring(r.length() - 1);
        }
        return r;
    }

    public static boolean isUUID(String uuidString) {
        try {
            UUID.fromString(uuidString);
        } catch (IllegalArgumentException ignored) {
            return false;
        }
        return true;
    }

    public static String loreListToString(List<String> strings) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < strings.size(); i++) {
            builder.append(strings.get(i));

            // New line if not end
            if (i != strings.size() - 1)
                builder.append("\n");
        }

        builder.toString();
    }

    public static List<String> splitLoreString(String toSplit) {
        return splitLoreString(toSplit, 23);
    }

    public static List<String> splitLoreString(String toSplit, int limit) {
        List<String> lore = new ArrayList<String>();
        String line = "";
        for (String word : toSplit.split(" ")) {
            if (line.length() > limit) {
                line = line.replaceFirst(" ", "");
                String color = ChatColor.WHITE.toString();
                if (lore.size() == 0) {
                    color = ChatColor.getLastColors(line);
                } else {
                    color = ChatColor.getLastColors(lore.get(lore.size() - 1));
                }
                lore.add(color + line.replace("|", " "));
                line = "";
            }
            line = line + " " + word;
        }
        line = line.replaceFirst(" ", "");
        lore.add(ChatColor.getLastColors(toSplit) + line.replace("|", " "));
        return lore;
    }

    public static String formatDouble(double number, int length) {
        StringBuilder formatter = new StringBuilder("#.");
        for (int i = 0; i < length; i++) {
            formatter.append("#");
        }
        return new DecimalFormat(formatter.toString()).format(number);
    }

    public static String convertToRoman(int mInt) {
        String[] rnChars = {"M", "CM", "D", "C", "XC", "L", "X", "IX", "V", "I"};
        int[] rnVals = {1000, 900, 500, 100, 90, 50, 10, 9, 5, 1};
        String retVal = "";

        for (int i = 0; i < rnVals.length; i++) {
            int numberInPlace = mInt / rnVals[i];
            if (numberInPlace == 0) {
                continue;
            }
            retVal += numberInPlace == 4 && i > 0 ? rnChars[i] + rnChars[i - 1] :
                    new String(new char[numberInPlace]).replace("\0", rnChars[i]);
            mInt = mInt % rnVals[i];
        }
        return retVal;
    }

}
