package com.illuzionzstudios.core.bukkit.util;

import lombok.Getter;
import lombok.Setter;

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
