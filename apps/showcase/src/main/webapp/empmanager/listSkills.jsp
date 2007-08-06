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
<%@ taglib prefix="s" uri="/struts-tags" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Available Skills</title></head>

<body>
<h1>Available Skills</h1>
<table>
    <tr>
        <th>Name</th><th>Description</th>
    </tr>
    <s:iterator value="availableItems">
        <tr>
            <td><a href="<s:url action="edit"><s:param name="skillName" value="name"/></s:url>"><s:property value="name"/></a></td>
            <td><s:property value="description"/></td>
        </tr>
    </s:iterator>
</table>
<!-- Although namescape not correctly specified, the following link should find the right action -->
<p><a href="<s:url action="edit" includeParams="none"/>">Create new Skill</a></p>
<p><a href="<s:url action="showcase" namespace="/" includeParams="none"/>">Back to Showcase Startpage</a></p>
</body>
</html>
