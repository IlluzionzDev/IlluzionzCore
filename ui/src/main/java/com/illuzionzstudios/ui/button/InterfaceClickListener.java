package com.illuzionzstudios.ui.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

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
    Can be registered (and called) via InterfaceButton (for a specific button) or UserInterface (for an entire part of a interface)
 */
public interface InterfaceClickListener {

    /**
     * Called via the listener on click
     */
    void onClick(Player player, InventoryClickEvent event);

}
