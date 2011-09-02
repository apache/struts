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

<input type="checkbox" <s:if test="parameters['nameValue']">checked="checked"</s:if>
   name="<s:property value="parameters['name']"/>"
   value="<s:property value="parameters['fieldValue']"/>"
    <s:if test="parameters['disabled']">disabled="disabled"</s:if>
    <s:if test="parameters['tabindex'] != null">tabindex="<s:property value="parameters['tabindex']"/>"</s:if>
    <s:if test="parameters['onchange'] != null">onchange="<s:property value="parameters['onchange']"/>"</s:if>
    <s:if test="parameters['onclick'] != null">onclick="<s:property value="parameters['onclick']"/>"</s:if>
    <s:if test="parameters['id'] != null">id="<s:property value="parameters['id']"/>"</s:if>
/>
