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
    <b>Accessing properties inside Arrays</b>
</p>
<p>
    To access properties inside arrays, use the brackets "[]" operators with the desired index(starting from 0). The action
    class has an array of String in the field <i>bands</i>.
</p>
<p>
    To access the second element in the <i>bands</i> array type:
</p>
<p>
    <i id="example0">
        bands[1]
    </i>
</p>
<p>
    on the OGNL console and hit enter. <a href="#" onclick="execOgnl('example0')">Do it for me</a>
</p>
<p>
    <b>Accessing properties inside Lists</b>
</p>
<p>Lists can be accessed on the same way. The action class has a List of String on the field <i>movies</i>.</p>
<p>
    To access the first element in the <i>movies</i> list type:
</p>
<p>
    <i id="example1">
        movies[0]
    </i>
</p>
<p>
    on the OGNL console and hit enter.  <a href="#" onclick="execOgnl('example1')">Do it for me</a>
</p>
<br/>
<p>
    <a href="#" onclick="window.open('http://www.ognl.org/2.6.9/Documentation/html/LanguageGuide/indexing.html#N10184')">[More details]</a>
</p>
