package com.illuzionzstudios.core.bukkit.item;

import com.illuzionzstudios.core.locale.Locale;
import com.illuzionzstudios.core.locale.player.Message;
import com.illuzionzstudios.core.plugin.IlluzionzPlugin;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

public class ItemStackFactory {
    private ItemStack item;

    public ItemStackFactory(ItemStack item) {
        this.item = item;
    }

    public ItemStackFactory(Material mat) {
        this(new ItemStack(mat));
    }

    public ItemStackFactory(Material mat, int data) {
        this(new ItemStack(mat));
        data(data);
    }

    public ItemStackFactory amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemStackFactory lore(String node) {
        String loreLine = IlluzionzPlugin.getInstance().getLocale().getMessageOrDefault(node, node).getMessage();
        List<String> newLore = new ArrayList<>();

        if (loreLine != null) {
            for (String translatedLine : StringUtils.split(loreLine, "\n")) {
                if (translatedLine != null) {
                    newLore.add(Locale.color(translatedLine));
                }
            }
        }

        ItemMeta meta = item.getItemMeta();
        meta.setLore(newLore);
        item.setItemMeta(meta);

        hideAttributes();
        return this;
    }

    public ItemStackFactory lore(Message message) {
        lore(message.getMessage());
        hideAttributes();
        return this;
    }

    public ItemStackFactory type(Material material) {
        item.setType(material);
        return this;
    }

    public ItemStackFactory rawLore(String... lines) {
        return rawLore(Arrays.asList(lines));
    }

    public ItemStackFactory rawLore(List<String> lines) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lines);
        item.setItemMeta(meta);

        hideAttributes();
        return this;
    }


    public ItemStackFactory addRawLore(String... toAdd) {
        return addLore(Arrays.asList(toAdd));
    }

    public ItemStackFactory addLore(List<String> toAdd) {
        ItemMeta meta = item.getItemMeta();

        List<String> lines = meta.getLore();
        lines.addAll(toAdd);
        meta.setLore(lines);

        hideAttributes();
        item.setItemMeta(meta);
        return this;
    }

    public ItemStackFactory setLore(List<String> toAdd) {
        ItemMeta meta = item.getItemMeta();

        meta.setLore(toAdd);

        hideAttributes();
        item.setItemMeta(meta);
        return this;
    }

    public ItemStackFactory data(short data) {
        item.setDurability(data);
        return this;
    }

    public ItemStackFactory data(int data) {
        return data((short) data);
    }

    public ItemStackFactory rawName(String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        hideAttributes();
        return this;
    }

    public ItemStackFactory name(String node) {
        rawName(IlluzionzPlugin.getInstance().getLocale().getMessageOrDefault(node, node).getMessage());
        hideAttributes();
        return this;
    }

    public ItemStackFactory name(Message message) {
        name(message.getMessage());
        hideAttributes();
        return this;
    }

    public void hideAttributes() {
        ItemMeta meta = item.getItemMeta();

        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(meta);
    }

    public ItemStackFactory setUnbreakable(boolean unbreakable) {
        ItemMeta meta = item.getItemMeta();

        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        meta.spigot().setUnbreakable(unbreakable);
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        return this;
    }

    /**
     * Add glow to the item
     */
    public ItemStackFactory glow() {
        ItemMeta meta = item.getItemMeta();

        meta.addEnchant(Enchantment.LURE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);

        hideAttributes();
        return this;
    }


    public ItemStack get() {
        return item;
    }

}
