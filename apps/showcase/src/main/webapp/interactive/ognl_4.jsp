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
    <b>Accessing properties on the stack</b>
</p>
<p>
   Object that are not on the top of the Value Stack are accessed using the "#name" notation. 
   Some objects are always pushed into the stack by Struts, like:
</p>
<ul>
    <li>#application</li>
    <li>#session</li>
    <li>#request</li>
    <li>#parameters</li>
</ul>
<p>To see the value of the first parameter type:</p>
<p>
    <i id="example">
        #parameters['debug'][0]
    </i>
</p>
<p>
    on the OGNL console and hit enter.  <a href="#" onclick="execOgnl()">Do it for me</a>
</p>
<br/>
<p>
    <a href="#" onclick="window.open('http://struts.apache.org/2.x/docs/ognl.html')">[More details]</a>
</p>