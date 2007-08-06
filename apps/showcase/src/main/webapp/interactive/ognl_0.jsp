<%
    request.setAttribute("decorator", "none");
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<%--
/*
 * $Id: pom.xml 559206 2007-07-24 21:01:18Z apetrelli $
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
--%>
<p>
    <b>Accessing properties</b>
</p>
<p>
    The framework uses a standard naming context to evaluate OGNL expressions. 
    The top level object dealing with OGNL is a Map (usually referred as a context map or context).
    OGNL has a notion of there being a root (or default) object within the context. 
    In OGNL expressions, the properties of the root object can be referenced without any special "marker" notion.
    References to other objects are marked with a pound sign (#).
    
    In this example (and in your JSP pages) the last action executed will be on the top of the stack. 
</p>
<p>    
    <a href="#" onclick="selectClassSrcTab()">This action</a> is available on the third tab above. 
    To access the <i>name</i> field type:
</p>
<p>
    <i id="example">
        name
    </i>
</p>
<p>
    on the OGNL console and hit enter. <a href="#" onclick="execOgnl()">Do it for me</a>
</p>