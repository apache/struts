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
// Copyright 2006 Google Inc. All Rights Reserved.

package org.apache.struts2.impl;

import junit.framework.TestCase;

import org.apache.struts2.Messages;

public class MessagesTest extends TestCase {

    public void testForField() {
        Messages messages = new MessagesImpl();
        Messages fieldMessages = messages.forField("foo");
        fieldMessages.addError("foo");
        assertFalse(fieldMessages.getErrors().isEmpty());
        assertTrue(messages.hasErrors());
    }

    public void testHasMessagesForSeverity() {
        for (Messages.Severity severity : Messages.Severity.values()) {
            Messages messages = new MessagesImpl();
            messages.add(severity, "foo");

            assertFalse(messages.isEmpty(severity));

            for (Messages.Severity other : Messages.Severity.values())
                if (other != severity)
                    assertTrue(messages.isEmpty(other));
        }
    }
}
