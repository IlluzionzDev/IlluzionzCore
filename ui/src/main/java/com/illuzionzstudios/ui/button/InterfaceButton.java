package com.illuzionzstudios.ui.button;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
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

/*
    An id represents the identifier for that item in the inventory. Can be null if it doesnt need to be referenced
        Any part of a interface can access a interfacebutton via ID (for an example, changing the icon after items are added)
    Listeners represent an action when the button is clicked
 */
@Getter
@Builder
@Setter
public class InterfaceButton {

    /**
     * Id to identify the button
     */
    private String id;

    /**
     * The slot this buton sits in
     */
    private int slot;

    /**
     * Icon to display in the interact
     */
    private ItemStack icon;

    /**
     * The listener that runs on click
     */
    private InterfaceClickListener listener;

    /**
     * Create a new instance of this exact instance
     */
    public InterfaceButton clone() {
        return new InterfaceButton(id, slot, icon, listener);
    }
}
