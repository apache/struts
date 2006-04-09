/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.action2.util;


/**
 * A bean that can be used to time execution of pages
 *
 * @author Rickard �berg (rickard@middleware-company.com)
 * @version $Revision$
 */
public class Timer {

    // Attributes ----------------------------------------------------
    long current = System.currentTimeMillis();
    long start = current;


    // Public --------------------------------------------------------
    public long getTime() {
        // Return how long time has passed since last check point
        long now = System.currentTimeMillis();
        long time = now - current;

        // Reset so that next time we get from this point
        current = now;

        return time;
    }

    public long getTotal() {
        // Reset start so that next time we get from this point
        return System.currentTimeMillis() - start;
    }
}
