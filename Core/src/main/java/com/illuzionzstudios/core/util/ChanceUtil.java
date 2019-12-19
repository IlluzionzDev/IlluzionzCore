package com.illuzionzstudios.core.util;

import java.util.Random;

/**
 * Created by Illuzionz on 12 2019
 */
public class ChanceUtil {

    /**
     * Given a chance, calculate out of 100 if to return yes.
     * Pretty much checking if a given chance occurs.
     */
    public static boolean calculateChance(double chance) {
        double value = 100 * new Random().nextDouble();
        return value <= chance;
    }

}
