package com.illuzionzstudios.core.bukkit.util;

import com.illuzionzstudios.core.scheduler.MinecraftScheduler;
import com.illuzionzstudios.core.util.StringUtil;
import lombok.ToString;

@ToString
public class Cooldown {
    //Represents the time (in mills) when the time will expire
    private long expireTicks = 0;
    private long expireTime = 0;

    public Cooldown() {
    }

    public Cooldown(int ticks) {
        setWait(ticks);
    }

    //Setters
    public void setWait(int ticks) {
        this.expireTicks = MinecraftScheduler.getCurrentTick() + ticks;
        this.expireTime = System.currentTimeMillis() + (ticks * 50);
    }

    public void reset() {
        this.expireTicks = 0;
    }

    //Getters
    public boolean isReady() {
        return getTickLeft() <= 0;
    }

    public boolean isReadyRealTime() {
        return getMillisecondsLeft() <= 0;
    }

    private long getMillisecondsLeft() {
        return expireTime - System.currentTimeMillis();
    }

    public long getTickLeft() {
        return expireTicks - MinecraftScheduler.getCurrentTick();
    }

    public String getFormattedTimeLeft(boolean verbose) {
        return StringUtil.getFormattedTime(getMillisecondsLeft(), verbose);
    }

}
