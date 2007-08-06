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
<title>Showcase - Tags - Non UI Tags</title>
</head>
<body>
<h1>Non UI Tags</h1>

<ul>
    <li><s:url var="url" action="showActionTagDemo" namespace="/tags/non-ui/actionTag"/><s:a href="%{url}">Action Tag</s:a></li>
    <li><s:url var="url" value="date.jsp" /><s:a href="%{url}">Date Tag</s:a></li>
    <li><s:url var="url" value="debug.jsp" /><s:a href="%{url}">Debug Tag</s:a></li>
    <li><s:url var="url" action="showGeneratorTagDemo" namespace="/tags/non-ui/iteratorGeneratorTag" /><s:a href="%{url}">Iterator Generator Tag</s:a></li>
    <li><s:url var="url" action="showAppendTagDemo" namespace="/tags/non-ui/appendIteratorTag" /><s:a href="%{#url}">Append Iterator Tag</s:a>
    <li><s:url var="url" action="showMergeTagDemo" namespace="/tags/non-ui/mergeIteratorTag" /><s:a href="%{#url}">Merge Iterator Demo</s:a>
    <li><s:url var="url" action="showSubsetTagDemo" namespace="/tags/non-ui/subsetIteratorTag" /><s:a href="%{#url}">Subset Tag</s:a>
    <li><s:url var="url" value="actionPrefix/index.jsp"/><s:a href="%{#url}">Action Prefix Example</s:a></li>
	<li><s:url var="url" action="testIfTagJsp" namespace="/tags/non-ui/ifTag"/><s:a href="%{#url}">If Tag (JSP)</s:a></li>
	<li><s:url var="url" action="testIfTagFreemarker" namespace="/tags/non-ui/ifTag"/><s:a href="%{#url}">If Tag (Freemarker)</s:a></li>
</ul>

</body>
</html>
