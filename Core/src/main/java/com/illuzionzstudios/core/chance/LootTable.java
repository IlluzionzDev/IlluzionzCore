package com.illuzionzstudios.core.chance;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */

/**
 * Used for chance systems
 */
public class LootTable<T> {

    private Random random = new Random();
    private List<Pair<T, Double>> lootTable = new LinkedList<>();
    private double totalWeight;

    /**
     * Add new loot with weight
     *
     * @param type   The type of loot based on LootTable
     * @param weight The weight as a double
     */
    public void addLoot(T type, double weight) {
        lootTable.add(new Pair<>(type, weight));
        totalWeight += weight;
    }

    /**
     * Pick random item from loottable based on weight
     */
    public T pick() {
        double currentItemUpperBound = 0;

        double nextValue = 0 + (totalWeight - 0) * random.nextDouble();
        for (Pair<T, Double> itemAndWeight : lootTable) {
            currentItemUpperBound += itemAndWeight.getValue();
            if (nextValue < currentItemUpperBound)
                return itemAndWeight.getKey();
        }

        return lootTable.get(lootTable.size() - 1).getKey();
    }

}
