package com.illuzionzstudios.core.scheduler;

/**
 * Copyright © 2018 Property of HQGAMING STUDIO, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 */

public interface Tickable {

    /**
     * Calls tick operation
     * Should be ran safely!
     */
    void tick() throws Exception;

}
