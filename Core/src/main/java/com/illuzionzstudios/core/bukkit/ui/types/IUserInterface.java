package com.illuzionzstudios.core.bukkit.ui.types;

import com.illuzionzstudios.core.bukkit.ui.button.InterfaceButton;
import com.illuzionzstudios.core.bukkit.ui.button.InterfaceClickListener;
import com.illuzionzstudios.core.scheduler.Tickable;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Map;

/**
 * Copyright © 2018 Property of HQGAMING STUDIO, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 */

public interface IUserInterface extends Tickable {

    /**
     * @return Returns the bukkit inventory
     */
    Inventory getInventory();


    /**
     * @return Returns all buttons
     */
    List<InterfaceButton> getButtons();

    /**
     * When interface is closed
     */
    boolean onClose();

    /**
     * Render interface
     */
    void render();

    /**
     * @return Can click items
     */
    boolean isClickable();

    /**
     * @return Returns the listener that's called whenever the players inventory is clicked
     */
    InterfaceClickListener getPlayerInventoryListener();


    /**
     * @return Returns the listener that's called whenever a specific slot (in top inventory) is clicked
     */
    Map<Integer, InterfaceClickListener> getSlotListeners();
}
