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
package org.apache.struts2.dispatcher.mapper;

import com.opensymphony.xwork2.Result;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.dispatcher.ServletRedirectResult;
import org.apache.struts2.views.jsp.StrutsMockHttpServletRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * DefaultActionMapper test case.
 *
 */
public class DefaultActionMapperTest extends StrutsTestCase {

    // =============================
    // === test name & namespace ===
    // =============================

    public void testParseNameAndNamespace1() throws Exception {
        ActionMapping actionMapping = new ActionMapping();

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.parseNameAndNamespace("someAction.action", actionMapping);

        assertEquals(actionMapping.getName(), "someAction");
        assertEquals(actionMapping.getNamespace(), "");
    }

    public void testParseNameAndNamespace2() throws Exception {
        ActionMapping actionMapping = new ActionMapping();

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.parseNameAndNamespace("/someAction.action", actionMapping);

        assertEquals(actionMapping.getName(), "someAction");
        assertEquals(actionMapping.getNamespace(), "/");
    }

    public void testParseNameAndNamespace3() throws Exception {
        ActionMapping actionMapping = new ActionMapping();

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        defaultActionMapper.parseNameAndNamespace("/one/two/three/someAction.action", actionMapping);

        assertEquals(actionMapping.getName(), "someAction");
        assertEquals(actionMapping.getNamespace(), "/one/two/three");
    }


    // ===========================
    // === test special prefix ===
    // ===========================

    public void testActionPrefix() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put(DefaultActionMapper.ACTION_PREFIX + "myAction", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setParameterMap(parameterMap);
        request.setupGetServletPath("/someServletPath.action");

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        ActionMapping actionMapping = defaultActionMapper.getMapping(request);

        assertEquals(actionMapping.getName(), "myAction");
    }

    public void testRedirectPrefix() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put(DefaultActionMapper.REDIRECT_PREFIX + "www.google.com", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setupGetServletPath("/someServletPath.action");
        request.setParameterMap(parameterMap);

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        ActionMapping actionMapping = defaultActionMapper.getMapping(request);

        Result result = actionMapping.getResult();
        assertNotNull(result);
        assertTrue(result instanceof ServletRedirectResult);

        //TODO: need to test location but there's noaccess to the property/method, unless we use reflection
    }

    public void testRedirectActionPrefix() throws Exception {
        Map parameterMap = new HashMap();
        parameterMap.put(DefaultActionMapper.REDIRECT_ACTION_PREFIX + "myAction", "");

        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        request.setupGetServletPath("/someServletPath.action");
        request.setParameterMap(parameterMap);

        DefaultActionMapper defaultActionMapper = new DefaultActionMapper();
        ActionMapping actionMapping = defaultActionMapper.getMapping(request);

        Result result = actionMapping.getResult();
        assertNotNull(result);
        assertTrue(result instanceof ServletRedirectResult);

        // TODO: need to test location but there's noaccess to the property/method, unless we use reflection
    }

    public void testGetUriFromActionMapper1() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("/myNamespace");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myNamespace/myActionName.action", uri);
    }

    public void testGetUriFromActionMapper2() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action", uri);
    }

    public void testGetUriFromActionMapper3() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action", uri);
    }


    public void testGetUriFromActionMapper4() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action", uri);
    }

    public void testGetUriFromActionMapper5() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action", uri);
    }

    //
    public void testGetUriFromActionMapper6() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("/myNamespace");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myNamespace/myActionName.action?test=bla", uri);
    }

    public void testGetUriFromActionMapper7() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action?test=bla", uri);
    }

    public void testGetUriFromActionMapper8() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setMethod("myMethod");
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action?test=bla", uri);
    }


    public void testGetUriFromActionMapper9() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action?test=bla", uri);
    }

    public void testGetUriFromActionMapper10() throws Exception {
        DefaultActionMapper mapper = new DefaultActionMapper();
        ActionMapping actionMapping = new ActionMapping();
        actionMapping.setName("myActionName?test=bla");
        actionMapping.setNamespace("/");
        String uri = mapper.getUriFromActionMapping(actionMapping);

        assertEquals("/myActionName.action?test=bla", uri);
    }

}
