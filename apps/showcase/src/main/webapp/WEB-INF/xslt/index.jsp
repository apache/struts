<!--
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
-->
<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
<title>Showcase - XSLT </title>
</head>
<body>
<h1> XSLT Result Showcase</h1>

<p>
    The XSLT result can be used to generate xml from the action. By default, each of the action's
    properties will be converted into a DOM and rendered.  These results can be transformed via
    xslt.  Additionally, the result's exposedValue parameter can be used to define an ognl expression
    which can be used to manipulate the object which will be converted to xml.

    <ul>
        <li><s:url var="url" namespace="/xslt" action="jvmInfo"/><s:a href="%{url}">Render the exposed portion of the action as html</s:a></li>
        <li><s:url var="url" namespace="/xslt" action="jvmInfoRaw"/><s:a href="%{url}">Render the exposed portion of the action as xml</s:a></li>
        <li><s:url var="url" namespace="/xslt" action="jvmInfoAll"/><s:a href="%{url}">Render the action as xml</s:a></li>
        <li><s:url var="url" namespace="/xslt" action="classpath"/><s:a href="%{url}">Render an ognl property</s:a></li>
    </ul>
</p>


</body>
</html>