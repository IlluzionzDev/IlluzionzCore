package com.illuzionzstudios.core.util;

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
