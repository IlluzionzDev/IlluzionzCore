package com.illuzionzstudios.core.bukkit.ui.button;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

/**
 * Copyright © 2018 Property of HQGAMING STUDIO, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 */

/*
    An id represents the identifier for that item in the inventory. Can be null if it doesnt need to be referenced
        Any part of a interface can access a interfacebutton via ID (for an example, changing the icon after items are added)
    Listeners represent an action when the button is clicked
 */
@Getter
@Builder
@Setter
public class InterfaceButton{
    private String id;
    private int slot;
    private ItemStack icon;
    private InterfaceClickListener listener;

    public InterfaceButton clone() {
        return new InterfaceButton(id, slot, icon, listener);
    }
}
