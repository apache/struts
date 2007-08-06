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
    <b><i>if</i> tag</b>
</p>
<p>
    The <i>if</i> tag allows you to optionally execute a JSP section. Multiple <i>elseif</i> tags
    and one <i>else</i> tag can be associated to an <i>if tag</i>.
</p>
<p>    
    To say hello to John Galt type:
</p>
<p>
    <pre id="example0">
        &lt;s:if test="name == 'John Galt'"&gt;
            Hi John
        &lt;/s:if&gt;
        &lt;s:else&gt;
            I don't know you!
        &lt;/s:else&gt;
    </pre>
</p>
<p>
    on the JSP console and hit enter. <a href="#" onclick="execJSP('example0')">Do it for me</a>
</p>
<p>
    <b><i>iterator</i> tag</b>
</p>
<p>
    The <i>iterator</i> tag loops over an <i>Iterable</i> object one object at a time into
    the Value Stack (the value will be on top of the stack).
</p>
<p>    
    To print the all the elements in the "bands" property type:
</p>
<p>
    <pre id="example1">
        &lt;s:iterator value="bands"&gt;
            &lt;s:property /&gt;
            &lt;br /&gt;
        &lt;/s:iterator&gt;
    </pre>
</p>
<p>
    on the JSP console and hit enter. <a href="#" onclick="execJSP('example1')">Do it for me</a>
</p>
<p>
    <a href="#" onclick="window.open('http://struts.apache.org/2.x/docs/property.html')">[More on the <i>if</i> tag]</a>
    <a href="#" onclick="window.open('http://struts.apache.org/2.x/docs/iterator.html')">[More on the <i>iterator</i> tag]</a>
</p>