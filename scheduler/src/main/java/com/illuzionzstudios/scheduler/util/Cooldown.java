package com.illuzionzstudios.scheduler.util;

import com.illuzionzstudios.core.util.StringUtil;
import com.illuzionzstudios.scheduler.MinecraftScheduler;
import lombok.ToString;

/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */

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
