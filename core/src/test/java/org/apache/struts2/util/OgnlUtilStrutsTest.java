/*
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
package org.apache.struts2.util;

import com.opensymphony.xwork2.ognl.OgnlUtil;
import org.apache.struts2.StrutsInternalTestCase;

public class OgnlUtilStrutsTest extends StrutsInternalTestCase {

    private OgnlUtil ognlUtil;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ognlUtil = container.getInstance(OgnlUtil.class);
    }

    public void testDefaultExcludes() {
        ognlUtil.setExcludedClasses("");
        ognlUtil.setExcludedPackageNames("");
        ognlUtil.setExcludedPackageNamePatterns("");
        assertTrue(ognlUtil.getExcludedClasses().size() > 0);
        assertTrue(ognlUtil.getExcludedPackageNames().size() > 0);

        try {
            ognlUtil.getExcludedClasses().clear();
        } catch (Exception ex){
            assertTrue(ex instanceof UnsupportedOperationException);
        }
        try {
            ognlUtil.getExcludedPackageNames().clear();
        } catch (Exception ex){
            assertTrue(ex instanceof UnsupportedOperationException);
        }
        try {
            ognlUtil.getExcludedPackageNamePatterns().clear();
        } catch (Exception ex){
            assertTrue(ex instanceof UnsupportedOperationException);
        }
    }
}