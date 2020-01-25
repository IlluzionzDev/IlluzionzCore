package com.illuzionzstudios.core.bukkit.util;

import lombok.Getter;
import lombok.Setter;

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
@Setter
public class PresetCooldown extends Cooldown {
    private int wait;

    public PresetCooldown(int defaultWait) {
        wait = defaultWait;
    }

    public void go() {
        super.setWait(wait);
    }
}
