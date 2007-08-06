<%--
/*
 * $Id$
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
<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
    <title>Showcase - Tags - UI Tags</title>
</head>
<body>
    <h1>UI Tags</h1>
    
    <ul>
        <li><s:url var="url" namespace="/tags/ui" action="example" method="input" /><s:a href="%{url}">UI Example</s:a></li>
        <li><s:url var="url" namespace="/tags/ui" action="exampleVelocity" method="input" /><s:a href="%{url}">UI Example (Velocity)</s:a></li>
        <li><s:url var="url" namespace="/tags/ui" action="lotsOfOptiontransferselect" method="input" /><s:a href="%{url}">Option Transfer Select UI Example</s:a></li>
        <li><s:url var="url" namespace="/tags/ui" action="moreSelects" method="input" /><s:a href="%{url}">More Select Box UI Examples</s:a></li>
        <li><s:url var="url" namespace="/tags/ui" value="treeExampleStatic.jsp" /><s:a href="%{url}">Tree Example (static)</s:a>
        <li><s:url var="url" namespace="/tags/ui" action="showDynamicTreeAction"/><s:a href="%{url}">Tree Example (dynamic)</s:a>
        <li><s:url var="url" namespace="/tags/ui" action="showDynamicAjaxTreeAction"/><s:a href="%{url}">Tree Example (dynamic ajax loading)</s:a>
        <li><s:url var="url" value="componentTagExample.jsp"/><s:a href="%{#url}">Component Tag Example</s:a>
        <li><a href="datepicker">DateTime picker tag - Pick a date</a></li>
        <li><a href="timepicker">DateTime picker tag - Pick a time</a></li>
        <%--li><s:url var="url" namespace="/tags/ui" action="populateUsingIterator" method="input" /><s:a href="%{url}">UI population using iterator tag</s:a></li--%>
    </ul>
</body>
</html>
