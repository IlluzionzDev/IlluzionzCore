package com.illuzionzstudios.core.locale.player;

import com.illuzionzstudios.compatibility.ServerVersion;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

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
 * The Message object. This holds the message to be sent
 * as well as the plugins prefix so that they can both be
 * easily manipulated then deployed
 */
public class Message {

    private static boolean canActionBar = false;

    static {
        try {
            Class.forName("net.md_5.bungee.api.ChatMessageType");
            Class.forName("net.md_5.bungee.api.chat.TextComponent");
            Player.Spigot.class.getDeclaredMethod("sendMessage", net.md_5.bungee.api.ChatMessageType.class, net.md_5.bungee.api.chat.TextComponent.class);
            canActionBar = true;
        } catch (Exception ex) {
        }
    }

    private String prefix = null;
    private String message;

    @Setter
    private int fadeIn;
    @Setter
    private int stay;
    @Setter
    private int fadeOut;

    /**
     * create a new message
     *
     * @param message the message text
     */
    public Message(String message) {
        this.message = message;
    }

    /**
     * Format and send the held message to a com.illuzionzstudios.data.player.
     * Detect if string is split and send multiple lines
     *
     * @param player com.illuzionzstudios.data.player to send the message to
     */
    public void sendMessage(Player player) {
        // Check for split lore
        String[] strings = getMessage().split("\n");
        player.sendMessage(strings);
    }

    /**
     * Format and send the held message with the
     * appended plugin prefix to a com.illuzionzstudios.data.player
     *
     * @param player com.illuzionzstudios.data.player to send the message to
     */
    public void sendPrefixedMessage(Player player) {
        player.sendMessage(this.getPrefixedMessage());
    }

    /**
     * Format and send the held message to a com.illuzionzstudios.data.player
     *
     * @param sender command sender to send the message to
     */
    public void sendMessage(CommandSender sender) {
        String[] strings = getMessage().split("\n");
        sender.sendMessage(strings);
    }

    /**
     * Format and send the held message to a com.illuzionzstudios.data.player as a title message
     *
     * @param sender command sender to send the message to
     */
    public void sendTitle(CommandSender sender) {
        if (sender instanceof Player) {
            if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11)) {
                ((Player) sender).sendTitle(getMessage(), "", fadeIn, stay, fadeOut);
            } else {
                ((Player) sender).sendTitle(getMessage(), "");
            }
        } else {
            sender.sendMessage(this.getMessage());
        }
    }

    /**
     * Format and send the held message to a com.illuzionzstudios.data.player as a title message
     *
     * @param sender   command sender to send the message to
     * @param subtitle Subtitle to send
     */
    public void sendTitle(CommandSender sender, Message subtitle) {
        if (sender instanceof Player) {
            if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11)) {
                ((Player) sender).sendTitle(getMessage(), subtitle.getMessage(), fadeIn, stay, fadeOut);
            } else {
                ((Player) sender).sendTitle(getMessage(), subtitle.getMessage());
            }
        } else {
            sender.sendMessage(this.getMessage());
        }
    }

    /**
     * Format and send the held message to a com.illuzionzstudios.data.player as an actionbar message
     *
     * @param sender command sender to send the message to
     */
    public void sendActionBar(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.getMessage());
        } else if (!canActionBar) {
            sendTitle(sender);
        } else {
            ((Player) sender).spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                    new net.md_5.bungee.api.chat.TextComponent(getMessage()));
        }
    }

    /**
     * Format and send the held message with the
     * appended plugin prefix to a command sender
     *
     * @param sender command sender to send the message to
     */
    public void sendPrefixedMessage(CommandSender sender) {
        sender.sendMessage(this.getPrefixedMessage());
    }

    /**
     * Format the held message and append the plugins
     * prefix
     *
     * @return the prefixed message
     */
    public String getPrefixedMessage() {
        return ChatColor.translateAlternateColorCodes('&', (prefix == null ? "" : this.prefix)
                + " " + this.message);
    }

    /**
     * Get and format the held message
     *
     * @return the message
     */
    public String getMessage() {
        return ChatColor.translateAlternateColorCodes('&', this.message);
    }

    /**
     * Get and format the held message
     *
     * @return the message
     */
    public List<String> getMessageLines() {
        return Arrays.asList(ChatColor.translateAlternateColorCodes('&', this.message).split("\n|\\|"));
    }

    /**
     * Get the held message
     *
     * @return the message
     */
    public String getUnformattedMessage() {
        return this.message;
    }

    /**
     * Replace the provided placeholder with the provided object. <br />
     * Interchangeably Supports {@code %value%} and {@code {value}}
     *
     * @param placeholder the placeholder to replace
     * @param replacement the replacement object
     * @return the modified Message
     */
    public Message processPlaceholder(String placeholder, Object replacement) {
        final String place = Matcher.quoteReplacement(placeholder);
        this.message = message.replaceAll("%" + place + "%|\\{" + place + "\\}", replacement == null ? "" : Matcher.quoteReplacement(replacement.toString()));
        return this;
    }

    public Message setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    @Override
    public String toString() {
        return this.message;
    }
}

