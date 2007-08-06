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
    <b>Accessing properties inside Maps</b>
</p>
<p>
    To access properties inside maps, use the brackets "[]" operators with the desired key. The action
    class has a map of Book objects in the field <i>books</i>.
</p>
<p>
    To access the book with key "Iliad" in the <i>books</i> map type:
</p>
<p>
    <i id="example0">
        books['Iliad']
    </i>
</p>
<p>
    on the OGNL console and hit enter. <a href="#" onclick="execOgnl('example0')">Do it for me</a>
</p>
<p>If the key does not have spaces in it, you can access an element in the map, using the dot "." operator.</p>
<p>
    To access the book with key "Iliad" in the <i>books</i> map type:
</p>
<p>
    <i id="example1">
        books.Iliad
    </i>
</p>
<p>
    on the OGNL console and hit enter. <a href="#" onclick="execOgnl('example1')">Do it for me</a>
</p>
<p>
    Note that the object returned is of type Book. If you want to access one of its properties, you can do so using the dot
    "." operator as you did before.</p>
<p>
    To access the <i>author</i> property of the book with key "Iliad" in the <i>books</i> map type:
</p>
<p>
    <i id="example2">
        books['Iliad'].author
    </i>
</p>
<p>
    on the OGNL console and hit enter. <a href="#" onclick="execOgnl('example2')">Do it for me</a>
</p>
<br/>
<p>
    <a href="#" onclick="window.open('http://www.ognl.org/2.6.9/Documentation/html/LanguageGuide/indexing.html#N10184')">[More details]</a>
</p>
