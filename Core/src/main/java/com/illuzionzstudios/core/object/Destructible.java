package com.illuzionzstudios.core.object;

/**
 * Copyright © 2018 Property of HQGAMING STUDIO, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 */

public interface Destructible {

    /**
     * @return Returns if object is valid
     */
    boolean isValid();


    /**
     * Destroys object
     * Design destroy method to clean up worse case scenarios
     */
    void destroy();

}
