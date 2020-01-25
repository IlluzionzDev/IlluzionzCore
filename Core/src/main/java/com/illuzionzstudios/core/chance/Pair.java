package com.illuzionzstudios.core.chance;

import lombok.Getter;

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
