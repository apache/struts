/*
 * Copyright 2002-2007,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.validator;


/**
 * This interface should be implemented by validators that can short-circuit the validator queue
 * that it is in.
 *
 * @author Mark Woon
 */
public interface ShortCircuitableValidator {

    /**
     * Sets whether this field validator should short circuit the validator queue
     * it's in if validation fails.
     *
     * @param shortcircuit <tt>true</tt> if this field validator should short circuit on
     *                     failure, <tt>false</tt> otherwise
     */
    public void setShortCircuit(boolean shortcircuit);

    /**
     * Gets whether this field validator should short circuit the validator queue
     * it's in if validation fails.
     *
     * @return <tt>true</tt> if this field validator should short circuit on failure,
     *         <tt>false</tt> otherwise
     */
    public boolean isShortCircuit();
}
