/*
 * $Id: WelcomeTest.java 418530 2006-07-01 23:58:13Z mrdon $
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
package example;

import junit.framework.TestCase;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <code>WelcomeTest</code>
 *
 */
public class WelcomeTest extends TestCase {

    public void testWelcome() throws Exception {
        Welcome welcome = new Welcome();
        String result = welcome.execute();
        assertEquals(ActionSupport.SUCCESS, result);
        assertEquals(welcome.getMessage(), Welcome.MESSAGE);
    }
}
