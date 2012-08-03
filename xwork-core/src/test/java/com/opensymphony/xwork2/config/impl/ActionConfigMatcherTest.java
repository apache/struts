/*
 * $Id$
 *
 * Copyright 1999-2004 The Apache Software Foundation.
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
package com.opensymphony.xwork2.config.impl;

import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.ResultConfig;

import java.util.HashMap;
import java.util.Map;

public class ActionConfigMatcherTest extends XWorkTestCase {

    // ----------------------------------------------------- Instance Variables
    private Map<String,ActionConfig> configMap;
    private ActionConfigMatcher matcher;
    
    // ----------------------------------------------------- Setup and Teardown
    @Override public void setUp() throws Exception {
        super.setUp();
        configMap = buildActionConfigMap();
        matcher = new ActionConfigMatcher(configMap);
    }

    @Override public void tearDown() throws Exception {
        super.tearDown();
    }

    // ------------------------------------------------------- Individual Tests
    // ---------------------------------------------------------- match()
    public void testNoMatch() {
        assertNull("ActionConfig shouldn't be matched", matcher.match("test"));
    }

    public void testNoWildcardMatch() {
        assertNull("ActionConfig shouldn't be matched", matcher.match("noWildcard"));
    }

    public void testShouldMatch() {
        ActionConfig matched = matcher.match("foo/class/method");

        assertNotNull("ActionConfig should be matched", matched);
        assertTrue("ActionConfig should have properties, had " +
                matched.getParams().size(), matched.getParams().size() == 2);
        assertTrue("ActionConfig should have interceptors",
                matched.getInterceptors().size() == 1);
        assertTrue("ActionConfig should have ex mappings",
                matched.getExceptionMappings().size() == 1);
        assertTrue("ActionConfig should have external refs",
                matched.getExceptionMappings().size() == 1);
        assertTrue("ActionConfig should have results",
                matched.getResults().size() == 1);
    }

    public void testCheckSubstitutionsMatch() {
        ActionConfig m = matcher.match("foo/class/method");

        assertTrue("Class hasn't been replaced", "foo.bar.classAction".equals(m.getClassName()));
        assertTrue("Method hasn't been replaced", "domethod".equals(m.getMethodName()));
        assertTrue("Package isn't correct", "package-class".equals(m.getPackageName()));

        assertTrue("First param isn't correct", "class".equals(m.getParams().get("first")));
        assertTrue("Second param isn't correct", "method".equals(m.getParams().get("second")));
        
        ExceptionMappingConfig ex = m.getExceptionMappings().get(0);
        assertTrue("Wrong name, was "+ex.getName(), "fooclass".equals(ex.getName()));
        assertTrue("Wrong result", "successclass".equals(ex.getResult()));
        assertTrue("Wrong exception", 
                "java.lang.methodException".equals(ex.getExceptionClassName()));
        assertTrue("First param isn't correct", "class".equals(ex.getParams().get("first")));
        assertTrue("Second param isn't correct", "method".equals(ex.getParams().get("second")));
        
        ResultConfig result = m.getResults().get("successclass");
        assertTrue("Wrong name, was "+result.getName(), "successclass".equals(result.getName()));
        assertTrue("Wrong classname", "foo.method".equals(result.getClassName()));
        assertTrue("First param isn't correct", "class".equals(result.getParams().get("first")));
        assertTrue("Second param isn't correct", "method".equals(result.getParams().get("second")));
        
    }

    public void testCheckMultipleSubstitutions() {
        ActionConfig m = matcher.match("bar/class/method/more");

        assertTrue("Method hasn't been replaced correctly: " + m.getMethodName(),
            "doclass_class".equals(m.getMethodName()));
    }
    
    public void testLooseMatch() {
        configMap.put("*!*", configMap.get("bar/*/**"));
        ActionConfigMatcher matcher = new ActionConfigMatcher(configMap, true);
        
        // exact match
        ActionConfig m = matcher.match("foo/class/method");
        assertNotNull("ActionConfig should be matched", m);
        assertTrue("Class hasn't been replaced "+m.getClassName(), "foo.bar.classAction".equals(m.getClassName()));
        assertTrue("Method hasn't been replaced", "domethod".equals(m.getMethodName()));
        
        // Missing last wildcard
        m = matcher.match("foo/class");
        assertNotNull("ActionConfig should be matched", m);
        assertTrue("Class hasn't been replaced", "foo.bar.classAction".equals(m.getClassName()));
        assertTrue("Method hasn't been replaced, "+m.getMethodName(), "do".equals(m.getMethodName()));
        
        // Simple mapping
        m = matcher.match("class!method");
        assertNotNull("ActionConfig should be matched", m);
        assertTrue("Class hasn't been replaced, "+m.getPackageName(), "package-class".equals(m.getPackageName()));
        assertTrue("Method hasn't been replaced", "method".equals(m.getParams().get("first")));
        
        // Simple mapping
        m = matcher.match("class");
        assertNotNull("ActionConfig should be matched", m);
        assertTrue("Class hasn't been replaced", "package-class".equals(m.getPackageName()));
        assertTrue("Method hasn't been replaced", "".equals(m.getParams().get("first")));
        
    }

    private Map<String,ActionConfig> buildActionConfigMap() {
        Map<String, ActionConfig> map = new HashMap<String,ActionConfig>();

        HashMap params = new HashMap();
        params.put("first", "{1}");
        params.put("second", "{2}");

        ActionConfig config = new ActionConfig.Builder("package-{1}", "foo/*/*", "foo.bar.{1}Action")
                .methodName("do{2}")
                .addParams(params)
                .addExceptionMapping(new ExceptionMappingConfig.Builder("foo{1}", "java.lang.{2}Exception", "success{1}")
                    .addParams(new HashMap(params))
                    .build())
                .addInterceptor(new InterceptorMapping(null, null))
                .addResultConfig(new ResultConfig.Builder("success{1}", "foo.{2}").addParams(params).build())
                .build();
        map.put("foo/*/*", config);
        
        config = new ActionConfig.Builder("package-{1}", "bar/*/**", "bar")
                .methodName("do{1}_{1}")
                .addParam("first", "{2}")
                .build();
        
        map.put("bar/*/**", config);
        
        map.put("noWildcard", new ActionConfig.Builder("", "", "").build());

        return map;
    }
}
