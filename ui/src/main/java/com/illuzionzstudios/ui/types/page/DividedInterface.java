package com.illuzionzstudios.ui.types.page;

import com.illuzionzstudios.core.bukkit.item.ItemStackFactory;
import com.illuzionzstudios.ui.button.InterfaceButton;
import com.illuzionzstudios.ui.types.UserInterface;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
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

@Getter
@NoArgsConstructor
public abstract class DividedInterface extends UserInterface {

    @Setter
    protected boolean isPaging = false;
    protected List<InterfaceExtent> interfaces = new ArrayList<>();

    @Setter
    protected ItemStack nextPage = new ItemStackFactory(Material.ARROW).name("ui.next").get();

    @Setter
    protected ItemStack prevPage = new ItemStackFactory(Material.ARROW).name("ui.prev").get();

    protected int page = -1;

    @Override
    public void render() {
        int place = 0;

        for (InterfaceButton button : buttons) {

            if (place + 2 > (inventory.getSize())) {
                List<InterfaceButton> appended = buttons.subList(place, buttons.size() - 1);

                buttons = new ArrayList<>(buttons.subList(0, place));

                InterfaceExtent ie = new InterfaceExtent(this instanceof InterfaceExtent ? ((InterfaceExtent) this).getParent() : this, new ArrayList<>(appended));
                getInterfaces().add(ie);

                InterfaceButton next = InterfaceButton.builder().slot(size()).icon(nextPage).listener((p, event) -> openPage(player, page + 1)
                ).build();


                ie.setPrevPage(prevPage);
                ie.setNextPage(nextPage);

                buttons.add(next);
                inventory.setItem(next.getSlot(), next.getIcon());
                return;
            }

            inventory.setItem(button.getSlot(), button.getIcon());
            place++;
        }
    }

    protected void openPage(Player player, int page) {
        if (page < 0) {
            if (this instanceof InterfaceExtent) {
                ((InterfaceExtent) this).getParent().open(player);
            } else {
                this.open(player);
            }
        } else {
            getInterfaces().get(page).open(player);
        }
    }
}
