/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2;

/**
 * Implemented by actions which may need to record errors or messages.
 *
 * <pre>
 *   static import ResultNames.*;
 *
 *   public class SetName implements MessageAware {
 *
 *     Messages messages;
 *     String name;
 *
 *     public String execute() {
 *       return SUCCESS;
 *     }
 *
 *     public void setName(String name) {
 *       if ("".equals(name))
 *         messages.forField("name").addError("name.required");
 *
 *       this.name = name;
 *     }
 *
 *     public void setMessages(Messages messages) {
 *       this.messages = messages;
 *     }
 *   }
 * </pre>
 *
 * @author crazybob@google.com (Bob Lee)
 */
public interface MessageAware {

    /**
     * Sets messages.
     *
     * @param messages messages
     */
    void setMessages(Messages messages);
}
