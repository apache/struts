<%--
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
--%>
<!-- Test a bunch of imports this page does not use, then the taglibs it does use -->
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="com.opensymphony.xwork2.util.AnnotationUtils" %>
<%@ page import="com.opensymphony.xwork2.util.ClassLoaderUtil" %>
<%@ page import="com.opensymphony.xwork2.util.ProxyUtil" %>
<%@ page import="com.opensymphony.xwork2.util.ResolverUtil" %>
<%@ page import="com.opensymphony.xwork2.util.TextParseUtil" %>
<%@ page import="com.opensymphony.xwork2.util.WildcardUtil" %>
<%@ page import="org.apache.struts2.StrutsConstants" %>
<%@ page import="org.apache.struts2.util.ComponentUtils" %>
<%@ page import="org.apache.struts2.util.ContainUtil" %>
<%@ page import="org.apache.struts2.util.StrutsUtil" %>
<%@ page import="org.apache.struts2.util.URLDecoderUtil" %>
<%@ page import="org.apache.struts2.views.velocity.VelocityStrutsUtil" %>
<%@ taglib prefix="r" uri="http://jakarta.apache.org/taglibs/request-1.0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Struts2 Embedded JSP Plugin - Complex Test Page</title>
</head>
<body>
<div class="fakeCSSClass">
    <h1>Embedded JSP - Test JSTL Set/If Tag - Borrows structure from Struts 2 Showcase</h1>
</div>
<div class="fakeCSSClass">
    <div class="fakeCSSClass2">
        <div class="fakeCSSClass3">
            <p>
            This is a jsp to test the JSTL If/Choose Tags. There's a few combination being tested.
            The characters in bold an non-bold should be the same.
            </p>
            <b>1 - Foo -</b>
            <c:if test="${true}">
                Foo
            </c:if>
            <c:if test="${false}">
                Bar
            </c:if>
            <br/>
            <b>2 - Bar -</b>
            <c:choose>
                <c:when test="${false}">
                Foo
                </c:when>    
                <c:otherwise>
                Bar
                </c:otherwise>
            </c:choose>
            <br/>
            <b>3 - FooFooFoo - </b>
            <c:choose>
                <c:when test="${true}">
                Foo
                    <c:choose>
                        <c:when test="${true}">
                        FooFoo
                        </c:when>
                        <c:otherwise>
                        BarBar
                        </c:otherwise>
                    </c:choose>
                </c:when>    
                <c:otherwise>
                Bar
                </c:otherwise>
            </c:choose>
            <br/>
            <b>4 - FooBarBar - </b>
            <c:choose>
                <c:when test="${true}">
                Foo
                    <c:choose>
                        <c:when test="${false}">
                        FooFoo
                        </c:when>
                        <c:otherwise>
                        BarBar
                        </c:otherwise>
                    </c:choose>
                </c:when>    
                <c:otherwise>
                Bar
                </c:otherwise>
            </c:choose>
            <br/>
            <b>5 - BarFooFoo - </b>
            <c:choose>
                <c:when test="${false}">
                Foo
                </c:when>    
                <c:otherwise>
                Bar
                    <c:choose>
                        <c:when test="${true}">
                        FooFoo
                        </c:when>
                        <c:otherwise>
                        BarBar
                        </c:otherwise>
                    </c:choose>
                </c:otherwise>
            </c:choose>
            <br/>
            <b>6 - BarBarBar - </b>
            <c:choose>
                <c:when test="${false}">
                Foo
                </c:when>    
                <c:otherwise>
                Bar
                    <c:choose>
                        <c:when test="${false}">
                        FooFoo
                        </c:when>
                        <c:otherwise>
                        BarBar
                        </c:otherwise>
                    </c:choose>
                </c:otherwise>
            </c:choose>
            <br/>
        </div>
    </div>
</div>
<div>
    Request headers:<br/>
    <r:headers id="hdrs">
       <jsp:getProperty name="hdrs" property="name"/> = <jsp:getProperty name="hdrs" property="header"/>
    </r:headers>
    <br/>
    User-Agent:<br/>
    <r:existsHeader name="User-Agent">
       User-Agent=<r:header name="User-Agent"/>
    </r:existsHeader>
    <r:existsHeader name="User-Agent" value="false">
       No User-Agent
    </r:existsHeader>
    <br/>
    Request parameters:<br/>
    <r:parameters id="param">
       <jsp:getProperty name="param" property="name"/> = <jsp:getProperty name="param" property="value"/>
    </r:parameters>
    <br/>
    Request attributes:<br/>
    <r:attributes id="att">
       <jsp:getProperty name="att" property="name"/> = <jsp:getProperty name="att" property="value"/>
    </r:attributes>
    <br/>
</div>
<div>
    Start set/out/if tests<br/>
    <c:set var = "testvalue1" scope = "page" value = "${'value1'}" />
    <c:set var = "testvalue2" scope = "page" value = "${'value2'}" />
    <c:set var = "testvalue3" scope = "page" value = "${'value3'}" />
    <c:set var = "testvalue4" scope = "page" value = "${'value4'}" />
    <c:set var = "testvalue5" scope = "page" value = "${'value5'}" />
    All test values:<br/>
    testvalue1: <c:out default="" value="${testvalue1}" /><br/>
    testvalue2: <c:out default="" value="${testvalue2}" /><br/>
    testvalue3: <c:out default="" value="${testvalue3}" /><br/>
    testvalue4: <c:out default="" value="${testvalue4}" /><br/>
    testvalue5: <c:out default="" value="${testvalue5}" /><br/>
    <c:if test="${testvalue1=='value1'}">
        testvalue1 set/if worked.<br/>
    </c:if>
    <c:if test="${testvalue2=='value2'}">
        testvalue2 set/if worked.<br/>
    </c:if>
    <c:if test="${testvalue3=='value3'}">
        testvalue3 set/if worked.<br/>
    </c:if>        
    <c:if test="${testvalue4=='value4'}">
        testvalue4 set/if worked.<br/>
    </c:if>
    <c:if test="${testvalue5=='value5'}">
        testvalue5 set/if worked.<br/>
    </c:if>
    <br/>
    End set/out/if tests<br/>
</div>
<div>
    <br/>
    Start include tests<br/>
    <jsp:include page="org/apache/struts2/simple0.jsp"/>
    <jsp:include page="org/apache/struts2/sub/simple0.jsp"/>
    <jsp:include page="org/apache/struts2/printParam.jsp">
        <jsp:param name="username" value="JG"/>
    </jsp:include>
    <%@ include file="org/apache/struts2/simple0.jsp" %>
    <jsp:include page="org/apache/struts2/scriptlet.jsp"/>
    <jsp:include page="org/apache/struts2/tag0.jsp"/>
    <jsp:include page="org/apache/struts2/beans.jsp"/>
    <jsp:include page="org/apache/struts2/el.jsp"/>
    <jsp:include page="org/apache/struts2/jstl.jsp"/>
    <br/>
    <br/>
    End include tests<br/>
</div>
</body>
</html>
