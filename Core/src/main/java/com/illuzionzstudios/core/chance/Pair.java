package com.illuzionzstudios.core.chance;

/**
 * Created by Illuzionz on 12 2019
 */

import lombok.Getter;

/**
 * Used for chances
 */
public class Pair<K, V> {

    @Getter
    private K key;

    @Getter
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

}
