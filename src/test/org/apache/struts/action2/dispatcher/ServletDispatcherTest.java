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
package org.apache.struts.action2.dispatcher;

import java.util.HashMap;
import java.util.Map;


/**
 * @author CameronBraid
 */
public class ServletDispatcherTest extends AbstractServletDispatcherTestCase {
    public String getConfigFilename() {
        return "org/apache/struts/action2/dispatcher/ServletDispatcherTest-xwork.xml";
    }

    public ServletDispatcher getServletDispatcher() {
        return new ServletDispatcher();
    }

    public String getServletPath() {
        return "/Test.action";
    }

//    public void testEncodingAndLocaleSetFromStrutsProperties() throws IOException, ServletException {
//        testServletDispatcher();
//        assertEquals("ISO-8859-1",ServletDispatcher.getEncoding());
//        assertEquals(Locale.GERMANY,ServletDispatcher.getLocale());
//    }

    protected Map getParameterMap() {
        Map map = new HashMap();
        map.put("foo", "bar");

        return map;
    }
}
