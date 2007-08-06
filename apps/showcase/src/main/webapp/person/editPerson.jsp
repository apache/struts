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

<html>
<head>
    <title>Edit Persons (batch-edit)</title>
</head>

<body>
<s:form action="editPerson" theme="simple" validate="false">

    <table>
        <tr>
            <th>ID</th>
            <th>First Name</th>
            <th>Last Name</th>
        </tr>
        <s:iterator var="p" value="persons">
            <tr>
                <td>
                    <s:property value="%{id}" />
                </td>
                <td>
                    <s:textfield label="First Name" name="persons(%{id}).name" value="%{name}" theme="simple" />
                </td>
                <td>
                    <s:textfield label="Last Name" name="persons(%{id}).lastName" value="%{lastName}" theme="simple"/>
                </td>
            </tr>
        </s:iterator>
    </table>

    <s:submit method="save" value="Save all persons"/>
</s:form>

<ul>
    <li><a href="newPerson!input.action">Create</a> a new person</li>
    <li><a href="listPeople.action">List</a> all people</li>
</ul>

</body>
</html>
