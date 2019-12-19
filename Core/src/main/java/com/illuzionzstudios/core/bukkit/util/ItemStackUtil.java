package com.illuzionzstudios.core.bukkit.util;

import com.illuzionzstudios.core.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Copyright © 2018 Property of HQGAMING STUDIO, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 */

public class ItemStackUtil {
    //Combines the lore into a single string (useful for .contains, etc)
    public static String getCombinedLore(ItemStack... items) {
        StringBuilder loreString = new StringBuilder();

        for (ItemStack i : items) {
            List<String> lore = i.getItemMeta().getLore();
            if (lore == null) {
                return "";
            }
            for (String s : lore) {
                loreString.append(s + " ");
            }
        }
        return loreString.toString();
    }

    public static boolean hasRoomInInventory(Player player, int stacks) {
        int empties = 0;
        for (ItemStack i : player.getInventory().getContents()) {
            if (i == null) {
                empties++;
            }
        }
        return stacks <= empties;
    }

    public static String getLore(ItemStack item) {
        List<String> lore = item.getItemMeta().getLore();
        StringBuilder loreString = new StringBuilder();
        if (lore == null) {
            return "";
        }
        for (String s : lore) {
            loreString.append(s + " ");
        }
        return loreString.toString();
    }


    public static boolean isSimilar(ItemStack item1, ItemStack item2) {
        if (item1 == null || item2 == null) {
            return false;
        }
        if (item1.getType() == item2.getType()) {
            if (item1.getDurability() == item2.getDurability()) {
                if (item1.hasItemMeta() && item2.hasItemMeta()) {
                    if (Objects.equals(item1.getItemMeta().getDisplayName(), item2.getItemMeta().getDisplayName())) {
                        if (item1.getItemMeta().hasLore() && item2.getItemMeta().hasLore()) {
                            return item1.getItemMeta().getLore().equals(item2.getItemMeta().getLore());
                        } else return !item1.getItemMeta().hasLore() && !item2.getItemMeta().hasLore();
                    }
                } else return !item1.hasItemMeta() && !item2.hasItemMeta();
            }
        }
        return false;
    }


    //Splits a single string into multiple lines. Maintains color on each line
    public static List<String> getSplitLore(String toSplit, int limit) {
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

    public static int getSlot(int x, int y) {
        return ((y - 1) * 9) + (x - 1);
    }

    public static boolean isArmor(ItemStack i) {
        return StringUtil.contains(i.getType().name(), "HELMET") || StringUtil.contains(i.getType().name(), "CHESTPLATE") || StringUtil.contains(i.getType().name(), "LEGGINGS") || StringUtil.contains(i.getType().name(), "BOOTS");
    }
}
