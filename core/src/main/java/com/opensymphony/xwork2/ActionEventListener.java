/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * Provides hooks for handling key action events
 */
public interface ActionEventListener {
    /**
     * Called after an action has been created. 
     * 
     * @param action The action
     * @param stack The current value stack
     * @return The action to use
     */
    public Object prepare(Object action, ValueStack stack);
    
    /**
     * Called when an exception is thrown by the action
     * 
     * @param t The exception/error that was thrown
     * @param stack The current value stack
     * @return A result code to execute, can be null
     */
    public String handleException(Throwable t, ValueStack stack);
}
