package com.illuzionzstudios.ui.types.page;

import com.illuzionzstudios.ui.button.InterfaceButton;
import lombok.Getter;
import org.bukkit.Bukkit;

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
public class InterfaceExtent extends DividedInterface {

    private final DividedInterface parent;
    private List<InterfaceButton> appendedElements = new ArrayList<>();

    public InterfaceExtent(DividedInterface parent, List<InterfaceButton> appendedElements) {
        this.parent = parent;
        this.appendedElements = appendedElements;
        this.player = parent.getPlayer();

        this.page = getInterfaces().size();
    }

    @Override
    public void generateInventory() {
        inventory = Bukkit.createInventory(player, round(appendedElements.size()), ""); // TODO: Pass in title

        addButton(InterfaceButton.builder().slot(0).icon(prevPage).listener((p, event) -> openPage(player, page - 1)
        ).build());

        int slot = 1;

        for (InterfaceButton button : appendedElements) {
            // Move buttons to make space for back button //
            button.setSlot(slot);
            addButton(button);
            slot++;
        }
    }

    public List<InterfaceExtent> getInterfaces() {
        return parent.getInterfaces();
    }

}
