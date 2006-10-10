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
