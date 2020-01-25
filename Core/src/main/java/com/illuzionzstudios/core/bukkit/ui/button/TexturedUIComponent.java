package com.illuzionzstudios.core.bukkit.ui.button;

import com.illuzionzstudios.core.bukkit.item.ItemStackFactory;
import com.illuzionzstudios.core.locale.player.Message;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */

@Getter
public class TexturedUIComponent {

    @Setter
    private ItemStackFactory item;
    private int slot;

    private InterfaceClickListener listener;

    /**
     * @param id   The interface ID
     * @param slot The slot ui should be in
     */
    public TexturedUIComponent(int id, int slot) {
        this.slot = slot;

        // Default type for texture UI component
        this.item = new ItemStackFactory(Material.DIAMOND_PICKAXE);

        this.item.data(id);
        this.item.rawName(" ");
        this.item.setUnbreakable(true);
    }

    public TexturedUIComponent(ItemStack stack, int slot, String name) {
        this.slot = slot;

        this.item(stack);
        this.name(name);
    }

    public TexturedUIComponent(int id, int slot, String name) {
        this(id, slot);
        this.name(name);
    }

    public TexturedUIComponent item(ItemStack stack) {
        this.item = new ItemStackFactory(stack);
        return this;
    }

    public TexturedUIComponent type(Material type) {
        this.item.type(type);
        return this;
    }

    public TexturedUIComponent lore(String lore) {
        this.item.lore(lore);
        return this;
    }

    public TexturedUIComponent lore(Message message) {
        this.item.lore(message);
        return this;
    }

    public TexturedUIComponent name(Message message) {
        this.item.name(message);
        return this;
    }

    public TexturedUIComponent name(String name) {
        this.item.name(name);
        return this;
    }

    public TexturedUIComponent listener(InterfaceClickListener listener) {
        this.listener = listener;
        return this;
    }

    public InterfaceButton getButton() {
        return InterfaceButton.builder().slot(slot).listener(listener).icon(item.get()).build();
    }
}
