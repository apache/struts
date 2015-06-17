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
package com.opensymphony.xwork2;

import java.io.Serializable;


/**
 * All results (except for <code>Action.NONE</code>) of an {@link Action} are mapped to a View implementation.
 * <p/>
 * Examples of Views might be:
 * <ul>
 * <li>SwingPanelView - pops up a new Swing panel</li>
 * <li>ActionChainView - executes another action</li>
 * <li>SerlvetRedirectView - redirects the HTTP response to a URL</li>
 * <li>ServletDispatcherView - dispatches the HTTP response to a URL</li>
 * </ul>
 *
 * @author plightbo
 */
public interface Result extends Serializable {

    /**
     * Represents a generic interface for all action execution results.
     * Whether that be displaying a webpage, generating an email, sending a JMS message, etc.
     *
     * @param invocation  the invocation context.
     * @throws Exception can be thrown.
     */
    public void execute(ActionInvocation invocation) throws Exception;

}
