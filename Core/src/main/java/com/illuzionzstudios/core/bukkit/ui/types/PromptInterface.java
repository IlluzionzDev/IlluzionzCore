package com.illuzionzstudios.core.bukkit.ui.types;

import com.illuzionzstudios.core.bukkit.ui.button.InterfaceButton;
import org.bukkit.Bukkit;
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

public abstract class PromptInterface extends UserInterface {

    private String title;

    public void load(String title, ItemStack confirmIcon, ItemStack rejectIcon) {
        this.title = title;

        addButton(InterfaceButton.builder().icon(confirmIcon)
                .listener((p1, event) -> {
                    onConfirm();
                    p1.closeInventory();
                })
                .slot(2).build());

        addButton(InterfaceButton.builder().icon(rejectIcon).slot(6).listener((p2, event) -> {
            onReject();
            p2.closeInventory();
        }).build());

    }

    @Override
    public void generateInventory() {
        inventory = Bukkit.createInventory(player, 9, title);
    }

    public abstract void onConfirm();

    public abstract void onReject();
}

