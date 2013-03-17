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

package org.apache.struts2.session;

import org.apache.struts2.JUnitTestAction;
import org.apache.struts2.StrutsJUnit4TestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * Calls SessionSet which sets a value into the session and afterwards calls SessionGet, which
 * should return this session value.
 *
 * In prior versions only one executeAction() call could happen in a single test case, because
 * either the session values were deleted or the wrong result would be returned (always the result of
 * the first action execution).
 */
public class StrutsJUnit4SessionTestCaseTest extends StrutsJUnit4TestCase<JUnitTestAction>{
    @Test
    public void testPersistingSessionValues() throws Exception {
        String output = executeAction("/sessiontest/sessionSet.action");
        Assert.assertEquals("sessionValue", output);

        this.finishExecution();

        String output2 = executeAction("/sessiontest/sessionGet.action");
        Assert.assertEquals("sessionValue", output2);
    }

    @Override
    protected String getConfigPath() {
        return "struts-session-values-test.xml";
    }
}
