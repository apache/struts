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
<title>Showcase - Non-UI Tag - Example Submited </title>
</head>
<body>

<h1>Example Submitted</h1>
<table>
    <s:label label="Name" name="name" /> 
    <s:label label="Birthday" name="birthday" />
    <tr>
        <td><label class="label">Wake up time:</label></td>
        <td>
           <s:date name="wakeup" format="hh:mm aa" />
        </td>
    </tr>
    <s:label label="Biography" name="bio" /> 
    <s:label label="Favourite Color" name="favouriteColor" /> 
    <s:label label="Friends" name="friends" /> 
    <s:label label="Legal Age" name="legalAge" /> 
    <s:label label="Region" name="region" /> 
    <s:label label="State" name="state" /> 
    <s:label label="Picture" name="picture" /> 
    <s:label label="Favourite Language" name="favouriteLanguage" />
    <s:label label="Favourite Vehical Type" name="favouriteVehicalType" />
    <s:label label="Favourite Vehical Specific" name="favouriteVehicalSpecific" />
    <tr>
        <td><label class="label">Favourite Cartoon Characters (Left):</label></td>
        <td>
            <s:iterator value="leftSideCartoonCharacters" status="stat">
                <s:property value="%{#stat.count}" />.<s:property value="top" />&nbsp;
            </s:iterator>
        </td>
    </tr>
    <tr>
        <td><label class="label">Favourite Cartoon Characters (Right):</label></td>
        <td>
            <s:iterator value="rightSideCartoonCharacters" status="stat">
                <s:property value="%{#stat.count}" />.<s:property value="top" />&nbsp;
            </s:iterator>
        </td>
    </tr>
    <s:label label="Thoughts" name="thoughts" />
    
</table>
</body>
</html>
